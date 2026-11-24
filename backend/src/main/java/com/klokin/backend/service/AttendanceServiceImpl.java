package com.klokin.backend.service;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.MonthSummary;
import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.domain.WorkDay;
import com.klokin.backend.domain.WorkDayCalculator;
import com.klokin.backend.repository.TimeEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final EmployeeService employeeService;
    private final TimeEventRepository timeEventRepository;
    private final Clock clock;

    public AttendanceServiceImpl(EmployeeService employeeService, TimeEventRepository timeEventRepository,
                                 Clock clock) {
        this.employeeService = employeeService;
        this.timeEventRepository = timeEventRepository;
        this.clock = clock;
    }

    // readOnly: one read-only transaction for the whole calculation, so lazy relations
    // (like each event's client) can still load while we work
    @Override
    @Transactional(readOnly = true)
    public MonthSummary getMonth(Long employeeId, YearMonth month) {
        Employee employee = employeeService.read(employeeId);
        Company company = employee.getCompany();
        ZoneId zone = company.zoneId();
        Instant now = clock.instant();
        YearMonth target = month != null ? month : YearMonth.from(now.atZone(zone));

        // The month starts and ends at local midnight, which we convert to UTC for the query
        Instant start = target.atDay(1).atStartOfDay(zone).toInstant();
        Instant end = target.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant();
        List<TimeEvent> events = timeEventRepository.findForEmployeeBetween(employeeId, start, end);

        // Bucket the events by local date: { 2026-11-02 -> [in, lunch, ...], 2026-11-03 -> [...] }
        Map<LocalDate, List<TimeEvent>> eventsByDay = events.stream()
                .collect(Collectors.groupingBy(event -> LocalDate.ofInstant(event.getOccurredAt(), zone)));

        List<WorkDay> days = new ArrayList<>();
        for (LocalDate day = target.atDay(1); !day.isAfter(target.atEndOfMonth()); day = day.plusDays(1)) {
            days.add(WorkDayCalculator.calculate(day, eventsByDay.getOrDefault(day, List.of()), company, now));
        }
        return MonthSummary.of(employee, target, days);
    }
}
