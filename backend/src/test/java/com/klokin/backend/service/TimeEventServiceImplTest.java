package com.klokin.backend.service;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.EventSource;
import com.klokin.backend.domain.EventType;
import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.dto.TimeEventDto.SyncEvent;
import com.klokin.backend.dto.TimeEventDto.SyncRequest;
import com.klokin.backend.dto.TimeEventDto.SyncResponse;
import com.klokin.backend.dto.TimeEventDto.SyncStatus;
import com.klokin.backend.factory.EmployeeFactory;
import com.klokin.backend.repository.TimeEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeEventServiceImplTest {

    @Mock
    private TimeEventRepository repository;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private ClientService clientService;
    @Mock
    private WorkLocationService workLocationService;

    @InjectMocks
    private TimeEventServiceImpl service;

    @BeforeEach
    void setUp() {
        Company company = new Company.Builder().setId(10L).setName("Demo Signs").setTimeZone("Africa/Johannesburg").build();
        Employee lee = new Employee.Builder()
                .copy(EmployeeFactory.create(company, "Lee", "Jacobs", null, null, null))
                .setId(1L)
                .build();
        when(employeeService.read(1L)).thenReturn(lee);
        when(workLocationService.getByCompany(10L)).thenReturn(List.of());
    }

    @Test
    void sync_savesNewEvent_andSkipsOneWeAlreadyHave() {
        String alreadySynced = UUID.randomUUID().toString();
        String brandNew = UUID.randomUUID().toString();
        when(repository.existsByEventUuid(alreadySynced)).thenReturn(true);
        when(repository.existsByEventUuid(brandNew)).thenReturn(false);

        SyncResponse response = service.sync(new SyncRequest(1L, List.of(clockIn(alreadySynced), clockIn(brandNew))));

        assertEquals(1, response.saved());
        assertEquals(1, response.duplicates());
        verify(repository, times(1)).save(any(TimeEvent.class));
    }

    @Test
    void sync_rejectsBadEvent_butStillSavesTheGoodOne() {
        when(repository.existsByEventUuid(anyString())).thenReturn(false);
        // Arriving on site without saying which client is invalid
        SyncEvent bad = new SyncEvent(UUID.randomUUID().toString(), EventType.SITE_ARRIVE, EventSource.MANUAL,
                Instant.now(), -33.0, 18.0, null, null);

        SyncResponse response = service.sync(new SyncRequest(1L, List.of(bad, clockIn(UUID.randomUUID().toString()))));

        assertEquals(1, response.saved());
        assertEquals(1, response.rejected());
        assertEquals(SyncStatus.REJECTED, response.results().get(0).status());
    }

    private SyncEvent clockIn(String uuid) {
        return new SyncEvent(uuid, EventType.CLOCK_IN, EventSource.AUTO_WIFI, Instant.now(), -33.093, 18.034, null, null);
    }
}
