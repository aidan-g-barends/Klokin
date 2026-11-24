package com.klokin.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

// A business using Klokin. Staff, sites and clock-ins all belong to one company,
// which is what lets many businesses share one system safely ("multi-tenant").
@Entity
@Table(name = "companies")
public class Company {

    // Lunch is only deducted from shifts longer than this (a 4-hour Saturday has no lunch)
    public static final int LUNCH_AFTER_MINUTES = 5 * 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // IANA time zone, e.g. "Africa/Johannesburg"; decides which local day a clock-in falls on
    @Column(nullable = false)
    private String timeZone;

    // Normal hours Monday to Friday
    @Column(nullable = false)
    private LocalTime weekdayStart;

    @Column(nullable = false)
    private LocalTime weekdayEnd;

    // Saturday hours; both null means the company doesn't work Saturdays
    private LocalTime saturdayStart;

    private LocalTime saturdayEnd;

    // Standard unpaid lunch break in minutes
    @Column(nullable = false)
    private int lunchMinutes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // JPA needs a no-argument constructor to load rows from the database.
    // It's protected so the rest of our code has to go through the Builder or the Factory.
    protected Company() {
    }

    private Company(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.timeZone = builder.timeZone;
        this.weekdayStart = builder.weekdayStart;
        this.weekdayEnd = builder.weekdayEnd;
        this.saturdayStart = builder.saturdayStart;
        this.saturdayEnd = builder.saturdayEnd;
        this.lunchMinutes = builder.lunchMinutes;
        this.createdAt = builder.createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public LocalTime getWeekdayStart() {
        return weekdayStart;
    }

    public LocalTime getWeekdayEnd() {
        return weekdayEnd;
    }

    public LocalTime getSaturdayStart() {
        return saturdayStart;
    }

    public LocalTime getSaturdayEnd() {
        return saturdayEnd;
    }

    public int getLunchMinutes() {
        return lunchMinutes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ZoneId zoneId() {
        return ZoneId.of(timeZone);
    }

    public boolean worksSaturdays() {
        return saturdayStart != null && saturdayEnd != null;
    }

    // Paid minutes in a normal day, e.g. 08:00 to 17:00 minus a 60 min lunch = 480.
    // Sundays (and Saturdays when not worked) are 0, so anything worked then counts as overtime.
    public int scheduledMinutesOn(DayOfWeek day) {
        return switch (day) {
            case SUNDAY -> 0;
            case SATURDAY -> worksSaturdays() ? paidMinutes(saturdayStart, saturdayEnd) : 0;
            default -> paidMinutes(weekdayStart, weekdayEnd);
        };
    }

    private int paidMinutes(LocalTime start, LocalTime end) {
        int span = (int) Duration.between(start, end).toMinutes();
        return span > LUNCH_AFTER_MINUTES ? span - lunchMinutes : span;
    }

    @Override
    public String toString() {
        return "Company{id=" + id + ", name='" + name + "', timeZone='" + timeZone + "'}";
    }

    public static class Builder {
        private Long id;
        private String name;
        private String timeZone;
        private LocalTime weekdayStart;
        private LocalTime weekdayEnd;
        private LocalTime saturdayStart;
        private LocalTime saturdayEnd;
        private int lunchMinutes;
        private Instant createdAt;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setTimeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder setWeekdayStart(LocalTime weekdayStart) {
            this.weekdayStart = weekdayStart;
            return this;
        }

        public Builder setWeekdayEnd(LocalTime weekdayEnd) {
            this.weekdayEnd = weekdayEnd;
            return this;
        }

        public Builder setSaturdayStart(LocalTime saturdayStart) {
            this.saturdayStart = saturdayStart;
            return this;
        }

        public Builder setSaturdayEnd(LocalTime saturdayEnd) {
            this.saturdayEnd = saturdayEnd;
            return this;
        }

        public Builder setLunchMinutes(int lunchMinutes) {
            this.lunchMinutes = lunchMinutes;
            return this;
        }

        public Builder setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        // Copies an existing company so we can change a few fields and keep the rest
        public Builder copy(Company company) {
            this.id = company.id;
            this.name = company.name;
            this.timeZone = company.timeZone;
            this.weekdayStart = company.weekdayStart;
            this.weekdayEnd = company.weekdayEnd;
            this.saturdayStart = company.saturdayStart;
            this.saturdayEnd = company.saturdayEnd;
            this.lunchMinutes = company.lunchMinutes;
            this.createdAt = company.createdAt;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }
}
