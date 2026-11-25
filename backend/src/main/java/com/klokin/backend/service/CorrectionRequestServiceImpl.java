package com.klokin.backend.service;

import com.klokin.backend.domain.CorrectionRequest;
import com.klokin.backend.domain.CorrectionStatus;
import com.klokin.backend.domain.EventSource;
import com.klokin.backend.domain.EventType;
import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.exception.NotFoundException;
import com.klokin.backend.factory.TimeEventFactory;
import com.klokin.backend.repository.CorrectionRequestRepository;
import com.klokin.backend.repository.TimeEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class CorrectionRequestServiceImpl implements CorrectionRequestService {

    private final CorrectionRequestRepository repository;
    private final TimeEventRepository timeEventRepository;
    private final Clock clock;

    public CorrectionRequestServiceImpl(CorrectionRequestRepository repository,
                                        TimeEventRepository timeEventRepository, Clock clock) {
        this.repository = repository;
        this.timeEventRepository = timeEventRepository;
        this.clock = clock;
    }

    @Override
    public CorrectionRequest create(CorrectionRequest request) {
        return repository.save(request);
    }

    @Override
    public CorrectionRequest read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Correction request " + id + " not found"));
    }

    @Override
    public List<CorrectionRequest> getByEmployee(Long employeeId) {
        return repository.findByEmployee_IdOrderByCreatedAtDesc(employeeId);
    }

    @Override
    public List<CorrectionRequest> getPendingForCompany(Long companyId) {
        return repository.findByEmployee_Company_IdAndStatusOrderByCreatedAtAsc(companyId, CorrectionStatus.PENDING);
    }

    // Approving adds the missing clock-in/out as new events. Nothing is edited or deleted,
    // and because hours are always calculated from events, the day fixes itself.
    // @Transactional: the new events and the status change save together, or not at all.
    @Override
    @Transactional
    public CorrectionRequest approve(Long id) {
        CorrectionRequest request = readPending(id);
        if (request.getClockIn() != null) {
            timeEventRepository.save(correctionEvent(request, EventType.CLOCK_IN, request.getClockIn()));
        }
        if (request.getClockOut() != null) {
            timeEventRepository.save(correctionEvent(request, EventType.CLOCK_OUT, request.getClockOut()));
        }
        return repository.save(review(request, CorrectionStatus.APPROVED));
    }

    @Override
    @Transactional
    public CorrectionRequest reject(Long id) {
        return repository.save(review(readPending(id), CorrectionStatus.REJECTED));
    }

    // A request can only be decided once
    private CorrectionRequest readPending(Long id) {
        CorrectionRequest request = read(id);
        if (!request.isPending()) {
            throw new IllegalArgumentException("Correction request " + id + " was already " + request.getStatus());
        }
        return request;
    }

    private TimeEvent correctionEvent(CorrectionRequest request, EventType type, LocalTime time) {
        // Local date + time in the company's zone -> the exact moment (Instant) it happened
        Instant occurredAt = request.getWorkDate().atTime(time)
                .atZone(request.getEmployee().getCompany().zoneId()).toInstant();
        return TimeEventFactory.create(UUID.randomUUID().toString(), request.getEmployee(), type,
                EventSource.CORRECTION, occurredAt, null, null, null,
                "Correction #" + request.getId(), List.of());
    }

    private CorrectionRequest review(CorrectionRequest request, CorrectionStatus status) {
        return new CorrectionRequest.Builder()
                .copy(request)
                .setStatus(status)
                .setReviewedAt(clock.instant())
                .build();
    }
}
