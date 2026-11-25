package com.klokin.backend.service;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.CorrectionRequest;
import com.klokin.backend.domain.CorrectionStatus;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.factory.CompanyFactory;
import com.klokin.backend.factory.CorrectionRequestFactory;
import com.klokin.backend.factory.EmployeeFactory;
import com.klokin.backend.repository.CorrectionRequestRepository;
import com.klokin.backend.repository.TimeEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrectionRequestServiceImplTest {

    @Mock
    private CorrectionRequestRepository repository;
    @Mock
    private TimeEventRepository timeEventRepository;

    private CorrectionRequestServiceImpl service;
    private CorrectionRequest forgotToClockOut;

    @BeforeEach
    void setUp() {
        service = new CorrectionRequestServiceImpl(repository, timeEventRepository, Clock.systemUTC());
        Company company = CompanyFactory.create("Demo Signs", null, null, null, null, null, null);
        Employee lee = EmployeeFactory.create(company, "Lee", "Jacobs", null, null, null);
        forgotToClockOut = new CorrectionRequest.Builder()
                .copy(CorrectionRequestFactory.create(lee, LocalDate.now().minusDays(7), null,
                        LocalTime.of(17, 0), "Forgot to clock out"))
                .setId(5L)
                .build();
    }

    @Test
    void approve_addsTheMissingClockOut() {
        when(repository.findById(5L)).thenReturn(Optional.of(forgotToClockOut));
        when(repository.save(any(CorrectionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(CorrectionStatus.APPROVED, service.approve(5L).getStatus());
        verify(timeEventRepository, times(1)).save(any(TimeEvent.class));
    }

    @Test
    void approve_alreadyRejected_throwsAndAddsNothing() {
        CorrectionRequest rejected = new CorrectionRequest.Builder()
                .copy(forgotToClockOut).setStatus(CorrectionStatus.REJECTED).build();
        when(repository.findById(5L)).thenReturn(Optional.of(rejected));

        assertThrows(IllegalArgumentException.class, () -> service.approve(5L));
        verify(timeEventRepository, never()).save(any(TimeEvent.class));
    }
}
