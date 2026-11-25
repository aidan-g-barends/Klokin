package com.klokin.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

// "My hours are wrong": an employee asks for a missing clock-in or clock-out to be added.
// The boss approves or rejects it, so nobody argues and there's a record of every fix.
@Entity
@Table(name = "correction_requests")
public class CorrectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // The day being fixed, in the company's local time
    @Column(nullable = false)
    private LocalDate workDate;

    // What the times should be; either one can be empty
    private LocalTime clockIn;

    private LocalTime clockOut;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrectionStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant reviewedAt;

    protected CorrectionRequest() {
    }

    private CorrectionRequest(Builder builder) {
        this.id = builder.id;
        this.employee = builder.employee;
        this.workDate = builder.workDate;
        this.clockIn = builder.clockIn;
        this.clockOut = builder.clockOut;
        this.reason = builder.reason;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.reviewedAt = builder.reviewedAt;
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public LocalTime getClockIn() {
        return clockIn;
    }

    public LocalTime getClockOut() {
        return clockOut;
    }

    public String getReason() {
        return reason;
    }

    public CorrectionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public boolean isPending() {
        return status == CorrectionStatus.PENDING;
    }

    @Override
    public String toString() {
        return "CorrectionRequest{id=" + id + ", workDate=" + workDate + ", status=" + status + "}";
    }

    public static class Builder {
        private Long id;
        private Employee employee;
        private LocalDate workDate;
        private LocalTime clockIn;
        private LocalTime clockOut;
        private String reason;
        private CorrectionStatus status;
        private Instant createdAt;
        private Instant reviewedAt;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setEmployee(Employee employee) {
            this.employee = employee;
            return this;
        }

        public Builder setWorkDate(LocalDate workDate) {
            this.workDate = workDate;
            return this;
        }

        public Builder setClockIn(LocalTime clockIn) {
            this.clockIn = clockIn;
            return this;
        }

        public Builder setClockOut(LocalTime clockOut) {
            this.clockOut = clockOut;
            return this;
        }

        public Builder setReason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder setStatus(CorrectionStatus status) {
            this.status = status;
            return this;
        }

        public Builder setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setReviewedAt(Instant reviewedAt) {
            this.reviewedAt = reviewedAt;
            return this;
        }

        public Builder copy(CorrectionRequest request) {
            this.id = request.id;
            this.employee = request.employee;
            this.workDate = request.workDate;
            this.clockIn = request.clockIn;
            this.clockOut = request.clockOut;
            this.reason = request.reason;
            this.status = request.status;
            this.createdAt = request.createdAt;
            this.reviewedAt = request.reviewedAt;
            return this;
        }

        public CorrectionRequest build() {
            return new CorrectionRequest(this);
        }
    }
}
