package com.klokin.backend.service;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.EventType;
import com.klokin.backend.domain.MonthSummary;
import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.factory.CompanyFactory;
import com.klokin.backend.factory.EmployeeFactory;
import com.klokin.backend.repository.TimeEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private EmployeeService employeeService;
    @Mock
    private TimeEventRepository timeEventRepository;

    @Test
    void getMonth_addsUpEveryDayOfTheMonth() {
        // A fixed clock in December, so every November day is in the past
        Clock clock = Clock.fixed(Instant.parse("2026-12-15T10:00:00Z"), ZoneOffset.UTC);
        AttendanceServiceImpl service = new AttendanceServiceImpl(employeeService, timeEventRepository, clock);

        Company company = CompanyFactory.create("Demo Signs", null, null, null, null, null, null);
        Employee lee = new Employee.Builder()
                .copy(EmployeeFactory.create(company, "Lee", "Jacobs", null, null, null))
                .setId(1L)
                .build();
        when(employeeService.read(1L)).thenReturn(lee);
        // Two full days, 08:00 to 17:00 local (06:00 to 15:00 UTC), no lunch logged
        when(timeEventRepository.findForEmployeeBetween(eq(1L), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(
                        event(EventType.CLOCK_IN, "2026-11-02T06:00:00Z"), event(EventType.CLOCK_OUT, "2026-11-02T15:00:00Z"),
                        event(EventType.CLOCK_IN, "2026-11-03T06:00:00Z"), event(EventType.CLOCK_OUT, "2026-11-03T15:00:00Z")));

        MonthSummary summary = service.getMonth(1L, YearMonth.of(2026, 11));

        assertEquals(30, summary.days().size());
        assertEquals(2, summary.daysWorked());
        assertEquals(960, summary.totalMinutes());
        assertEquals(16.0, summary.totalHours());
    }

    private TimeEvent event(EventType type, String utcTime) {
        return new TimeEvent.Builder().setType(type).setOccurredAt(Instant.parse(utcTime)).build();
    }
}
