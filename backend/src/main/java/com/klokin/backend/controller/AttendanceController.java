package com.klokin.backend.controller;

import com.klokin.backend.domain.MonthSummary;
import com.klokin.backend.service.AttendanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/employees/{employeeId}/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    // GET /api/employees/1/attendance?month=2026-11  (leave month out for the current month)
    @GetMapping
    public MonthSummary getMonth(@PathVariable Long employeeId,
                                 @RequestParam(required = false) String month) {
        return service.getMonth(employeeId, parseMonth(month));
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return null;
        }
        try {
            return YearMonth.parse(month.trim());
        } catch (DateTimeParseException e) {
            // Becomes a 400 through the GlobalExceptionHandler
            throw new IllegalArgumentException("Month must look like 2026-11");
        }
    }
}
