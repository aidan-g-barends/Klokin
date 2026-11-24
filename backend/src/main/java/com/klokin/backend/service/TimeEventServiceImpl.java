package com.klokin.backend.service;

import com.klokin.backend.domain.Client;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.domain.WorkLocation;
import com.klokin.backend.dto.TimeEventDto.SyncEvent;
import com.klokin.backend.dto.TimeEventDto.SyncRequest;
import com.klokin.backend.dto.TimeEventDto.SyncResponse;
import com.klokin.backend.dto.TimeEventDto.SyncResult;
import com.klokin.backend.dto.TimeEventDto.SyncStatus;
import com.klokin.backend.exception.NotFoundException;
import com.klokin.backend.factory.TimeEventFactory;
import com.klokin.backend.repository.TimeEventRepository;
import com.klokin.backend.util.Helper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TimeEventServiceImpl implements TimeEventService {

    private static final int MAX_EVENTS_PER_SYNC = 500;
    private static final int MAX_FEED_SIZE = 200;

    private final TimeEventRepository repository;
    private final EmployeeService employeeService;
    private final ClientService clientService;
    private final WorkLocationService workLocationService;

    public TimeEventServiceImpl(TimeEventRepository repository, EmployeeService employeeService,
                                ClientService clientService, WorkLocationService workLocationService) {
        this.repository = repository;
        this.employeeService = employeeService;
        this.clientService = clientService;
        this.workLocationService = workLocationService;
    }

    // @Transactional: the whole batch runs in one database transaction
    @Override
    @Transactional
    public SyncResponse sync(SyncRequest request) {
        Helper.requireNotNull(request, "Sync request");
        Helper.requireNotNull(request.employeeId(), "Employee id");
        List<SyncEvent> events = request.events() != null ? request.events() : List.of();
        if (events.size() > MAX_EVENTS_PER_SYNC) {
            throw new IllegalArgumentException("Send at most " + MAX_EVENTS_PER_SYNC + " events per sync");
        }

        Employee employee = employeeService.read(request.employeeId());
        // Loaded once for the whole batch, not once per event
        List<WorkLocation> workLocations = workLocationService.getByCompany(employee.getCompany().getId());

        Set<String> seenInBatch = new HashSet<>();
        List<SyncResult> results = new ArrayList<>();
        for (SyncEvent item : events) {
            results.add(syncOne(item, employee, workLocations, seenInBatch));
        }
        return SyncResponse.of(results);
    }

    // Each event gets its own result: one bad event must never stop the good ones from saving.
    // Offline phones send days of events at once, so "all or nothing" would lose real work.
    private SyncResult syncOne(SyncEvent item, Employee employee, List<WorkLocation> workLocations,
                               Set<String> seenInBatch) {
        String uuid = item.eventUuid() != null ? item.eventUuid().trim().toLowerCase() : null;

        // Already stored: the phone retried after a dropped connection. That's fine, not an error.
        if (uuid != null && (seenInBatch.contains(uuid) || repository.existsByEventUuid(uuid))) {
            return new SyncResult(uuid, SyncStatus.DUPLICATE, "Already received");
        }

        try {
            Client client = item.clientId() != null ? clientService.read(item.clientId()) : null;
            TimeEvent event = TimeEventFactory.create(uuid, employee, item.type(), item.source(),
                    item.occurredAt(), item.latitude(), item.longitude(), client, item.note(), workLocations);
            repository.save(event);
            seenInBatch.add(uuid);
            return new SyncResult(uuid, SyncStatus.SAVED,
                    event.getFlags() != null ? "Flagged: " + event.getFlags() : null);
        } catch (IllegalArgumentException | NotFoundException e) {
            return new SyncResult(uuid, SyncStatus.REJECTED, e.getMessage());
        }
    }

    @Override
    public List<TimeEvent> getRecentForCompany(Long companyId, int limit) {
        // Keep the page size sensible whatever the caller asks for
        int size = Math.max(1, Math.min(limit, MAX_FEED_SIZE));
        return repository.findRecentForCompany(companyId, PageRequest.of(0, size));
    }
}
