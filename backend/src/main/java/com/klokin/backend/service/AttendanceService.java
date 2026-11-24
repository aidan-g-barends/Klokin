package com.klokin.backend.service;

import com.klokin.backend.domain.MonthSummary;

import java.time.YearMonth;

public interface AttendanceService {

    // month null = the current month in the company's time zone
    MonthSummary getMonth(Long employeeId, YearMonth month);
}
