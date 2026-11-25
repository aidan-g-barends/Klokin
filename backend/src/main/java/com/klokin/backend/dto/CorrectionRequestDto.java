package com.klokin.backend.dto;

import com.klokin.backend.domain.CorrectionRequest;
import com.klokin.backend.domain.CorrectionStatus;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.factory.CorrectionRequestFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public final class CorrectionRequestDto {

    private CorrectionRequestDto() {
    }

    public record Request(LocalDate workDate, LocalTime clockIn, LocalTime clockOut, String reason) {

        public CorrectionRequest toCorrection(Employee employee) {
            return CorrectionRequestFactory.create(employee, workDate, clockIn, clockOut, reason);
        }
    }

    public record Response(Long id,
                           Long employeeId,
                           String employeeName,
                           LocalDate workDate,
                           LocalTime clockIn,
                           LocalTime clockOut,
                           String reason,
                           CorrectionStatus status,
                           Instant createdAt,
                           Instant reviewedAt) {

        public static Response from(CorrectionRequest request) {
            return new Response(request.getId(), request.getEmployee().getId(),
                    request.getEmployee().getFullName(), request.getWorkDate(),
                    request.getClockIn(), request.getClockOut(), request.getReason(),
                    request.getStatus(), request.getCreatedAt(), request.getReviewedAt());
        }
    }
}
