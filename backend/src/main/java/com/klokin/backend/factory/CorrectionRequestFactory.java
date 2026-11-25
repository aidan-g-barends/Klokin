package com.klokin.backend.factory;

import com.klokin.backend.domain.CorrectionRequest;
import com.klokin.backend.domain.CorrectionStatus;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.util.Helper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public final class CorrectionRequestFactory {

    private static final int MAX_REASON_LENGTH = 500;

    private CorrectionRequestFactory() {
    }

    public static CorrectionRequest create(Employee employee, LocalDate workDate,
                                           LocalTime clockIn, LocalTime clockOut, String reason) {
        Helper.requireNotNull(employee, "Employee");
        Helper.requireNotNull(workDate, "Work date");
        Helper.requireText(reason, "Reason");
        if (reason.length() > MAX_REASON_LENGTH) {
            throw new IllegalArgumentException("Reason can be at most " + MAX_REASON_LENGTH + " characters");
        }
        if (clockIn == null && clockOut == null) {
            throw new IllegalArgumentException("Give the clock-in time, the clock-out time, or both");
        }
        if (clockIn != null && clockOut != null && !clockIn.isBefore(clockOut)) {
            throw new IllegalArgumentException("Clock-in must be before clock-out");
        }

        // You can only fix time that has already happened, in the company's own time zone
        ZoneId zone = employee.getCompany().zoneId();
        LocalDateTime now = LocalDateTime.now(zone);
        if (isAfter(workDate, clockIn, now) || isAfter(workDate, clockOut, now)) {
            throw new IllegalArgumentException("You can't correct a time that hasn't happened yet");
        }

        return new CorrectionRequest.Builder()
                .setEmployee(employee)
                .setWorkDate(workDate)
                .setClockIn(clockIn)
                .setClockOut(clockOut)
                .setReason(reason.trim())
                .setStatus(CorrectionStatus.PENDING)
                .setCreatedAt(Instant.now())
                .build();
    }

    private static boolean isAfter(LocalDate date, LocalTime time, LocalDateTime now) {
        return time != null && LocalDateTime.of(date, time).isAfter(now);
    }
}
