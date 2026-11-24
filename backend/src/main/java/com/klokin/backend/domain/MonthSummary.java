package com.klokin.backend.domain;

import java.time.YearMonth;
import java.util.List;

// Everything the heatmap screen needs: one entry per day plus the "201 hours" style totals
public record MonthSummary(Long employeeId,
                           String employeeName,
                           String month,
                           int totalMinutes,
                           double totalHours,
                           int overtimeMinutes,
                           int daysWorked,
                           List<WorkDay> days) {

    public static MonthSummary of(Employee employee, YearMonth month, List<WorkDay> days) {
        int totalMinutes = days.stream().mapToInt(WorkDay::workedMinutes).sum();
        int overtimeMinutes = days.stream().mapToInt(WorkDay::overtimeMinutes).sum();
        int daysWorked = (int) days.stream()
                .filter(day -> day.status() == DayStatus.WORKED || day.status() == DayStatus.IN_PROGRESS)
                .count();
        // Rounded to one decimal place: 12 090 minutes -> 201.5 hours
        double totalHours = Math.round(totalMinutes / 6.0) / 10.0;

        return new MonthSummary(employee.getId(), employee.getFullName(), month.toString(),
                totalMinutes, totalHours, overtimeMinutes, daysWorked, List.copyOf(days));
    }
}
