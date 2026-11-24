package com.klokin.backend.domain;

import com.klokin.backend.factory.CompanyFactory;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkDayCalculatorTest {

    // Mon to Fri 08:00 to 17:00 with a 60 min lunch, Saturday 09:00 to 13:00
    private final Company company = CompanyFactory.create("Demo Signs", null, null, null,
            LocalTime.of(9, 0), LocalTime.of(13, 0), null);

    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 23);
    private static final LocalDate SATURDAY = LocalDate.of(2026, 11, 28);
    private static final Instant NEXT_MONTH = Instant.parse("2026-12-15T10:00:00Z");

    @Test
    void normalDayWithLunch_isEightHoursNoOvertime() {
        WorkDay day = calculate(MONDAY, NEXT_MONTH,
                at(MONDAY, EventType.CLOCK_IN, "08:00"), at(MONDAY, EventType.LUNCH_START, "13:00"),
                at(MONDAY, EventType.LUNCH_END, "14:00"), at(MONDAY, EventType.CLOCK_OUT, "17:00"));

        assertEquals(DayStatus.WORKED, day.status());
        assertEquals(480, day.workedMinutes());
        assertEquals(0, day.overtimeMinutes());
    }

    @Test
    void lateFinish_countsOvertime() {
        WorkDay day = calculate(MONDAY, NEXT_MONTH,
                at(MONDAY, EventType.CLOCK_IN, "08:00"), at(MONDAY, EventType.LUNCH_START, "13:00"),
                at(MONDAY, EventType.LUNCH_END, "14:00"), at(MONDAY, EventType.CLOCK_OUT, "19:00"));

        assertEquals(600, day.workedMinutes());
        assertEquals(120, day.overtimeMinutes());
    }

    @Test
    void lunchCutShort_onlyTakesOffWhatWasTaken() {
        WorkDay day = calculate(MONDAY, NEXT_MONTH,
                at(MONDAY, EventType.CLOCK_IN, "08:00"), at(MONDAY, EventType.LUNCH_START, "13:00"),
                at(MONDAY, EventType.LUNCH_END, "13:20"), at(MONDAY, EventType.CLOCK_OUT, "17:00"));

        assertEquals(20, day.lunchMinutes());
        assertEquals(520, day.workedMinutes());
        assertEquals(40, day.overtimeMinutes());
    }

    @Test
    void noLunchLogged_stillTakesOffStandardLunch() {
        WorkDay day = calculate(MONDAY, NEXT_MONTH,
                at(MONDAY, EventType.CLOCK_IN, "08:00"), at(MONDAY, EventType.CLOCK_OUT, "17:00"));

        assertEquals(60, day.lunchMinutes());
        assertEquals(480, day.workedMinutes());
    }

    @Test
    void forgotToClockOut_isIncomplete() {
        WorkDay day = calculate(MONDAY, NEXT_MONTH, at(MONDAY, EventType.CLOCK_IN, "08:00"));

        assertEquals(DayStatus.INCOMPLETE, day.status());
        assertEquals(0, day.workedMinutes());
    }

    @Test
    void saturdayMorning_hasNoLunchAndNoOvertime() {
        WorkDay day = calculate(SATURDAY, NEXT_MONTH,
                at(SATURDAY, EventType.CLOCK_IN, "09:00"), at(SATURDAY, EventType.CLOCK_OUT, "13:00"));

        assertEquals(240, day.workedMinutes());
        assertEquals(0, day.lunchMinutes());
        assertEquals(0, day.overtimeMinutes());
    }

    @Test
    void stillAtWorkToday_countsUpToNow() {
        Instant tenAm = localInstant(MONDAY, "10:00");

        WorkDay day = calculate(MONDAY, tenAm, at(MONDAY, EventType.CLOCK_IN, "08:00"));

        assertEquals(DayStatus.IN_PROGRESS, day.status());
        assertEquals(120, day.workedMinutes());
    }

    @Test
    void daysWithoutEvents_areAbsentOrOff() {
        LocalDate sunday = MONDAY.minusDays(1);

        assertEquals(DayStatus.ABSENT, WorkDayCalculator.calculate(MONDAY, List.of(), company, NEXT_MONTH).status());
        assertEquals(DayStatus.OFF, WorkDayCalculator.calculate(sunday, List.of(), company, NEXT_MONTH).status());
    }

    private WorkDay calculate(LocalDate date, Instant now, TimeEvent... events) {
        return WorkDayCalculator.calculate(date, List.of(events), company, now);
    }

    // A bare event is enough here: the calculator only looks at type, time and client
    private TimeEvent at(LocalDate date, EventType type, String localTime) {
        return new TimeEvent.Builder().setType(type).setOccurredAt(localInstant(date, localTime)).build();
    }

    private Instant localInstant(LocalDate date, String localTime) {
        return LocalDateTime.of(date, LocalTime.parse(localTime)).atZone(company.zoneId()).toInstant();
    }
}
