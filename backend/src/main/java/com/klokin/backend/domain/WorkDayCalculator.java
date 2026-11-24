package com.klokin.backend.domain;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// The heart of Klokin: turns one day's raw events into paid hours, lunch and overtime.
// Pure Java with no database or Spring, so it's quick to test and easy to reason about.
public final class WorkDayCalculator {

    private WorkDayCalculator() {
    }

    public static WorkDay calculate(LocalDate date, List<TimeEvent> events, Company company, Instant now) {
        ZoneId zone = company.zoneId();
        LocalDate today = LocalDate.ofInstant(now, zone);
        int scheduledMinutes = company.scheduledMinutesOn(date.getDayOfWeek());

        if (events.isEmpty()) {
            return WorkDay.withoutEvents(date, statusWithoutEvents(date, today, scheduledMinutes));
        }

        // Walk through the day in time order, like reading a timeline left to right
        List<TimeEvent> timeline = events.stream()
                .sorted(Comparator.comparing(TimeEvent::getOccurredAt))
                .toList();

        Instant workStart = null;   // set while clocked in
        Instant lunchStart = null;  // set while on lunch
        long workedSeconds = 0;
        long lunchSeconds = 0;
        boolean lunchLogged = false;
        Instant firstIn = null;
        Instant lastOut = null;
        Set<String> sites = new LinkedHashSet<>();  // keeps order, ignores repeats

        for (TimeEvent event : timeline) {
            Instant at = event.getOccurredAt();
            switch (event.getType()) {
                case CLOCK_IN -> {
                    // A second clock-in while already in (e.g. Wi-Fi and GPS both fired) is ignored
                    if (workStart == null) {
                        workStart = at;
                        if (firstIn == null) {
                            firstIn = at;
                        }
                    }
                }
                case LUNCH_START -> {
                    if (workStart != null && lunchStart == null) {
                        lunchStart = at;
                        lunchLogged = true;
                    }
                }
                case LUNCH_END -> {
                    if (lunchStart != null) {
                        lunchSeconds += secondsBetween(lunchStart, at);
                        lunchStart = null;
                    }
                }
                case CLOCK_OUT -> {
                    if (workStart != null) {
                        // Clocking out during lunch ends the lunch too
                        if (lunchStart != null) {
                            lunchSeconds += secondsBetween(lunchStart, at);
                            lunchStart = null;
                        }
                        workedSeconds += secondsBetween(workStart, at);
                        workStart = null;
                        lastOut = at;
                    }
                }
                case SITE_ARRIVE -> {
                    if (event.getClient() != null) {
                        sites.add(event.getClient().getName());
                    }
                }
                case SITE_LEAVE -> {
                    // Nothing to add: site visits and the travel between them are already
                    // inside the clocked-in day, so they count as work automatically
                }
            }
        }

        DayStatus status;
        if (workStart != null && date.isEqual(today)) {
            // Still at work right now, so count up to this moment
            workedSeconds += secondsBetween(workStart, now);
            if (lunchStart != null) {
                lunchSeconds += secondsBetween(lunchStart, now);
            }
            status = DayStatus.IN_PROGRESS;
        } else if (workStart != null || firstIn == null) {
            // Never clocked out (or only site events): the open part isn't counted until it's corrected
            status = DayStatus.INCOMPLETE;
        } else {
            status = DayStatus.WORKED;
        }

        int grossMinutes = (int) (workedSeconds / 60);
        int lunchMinutes = lunchMinutes(lunchLogged, lunchSeconds, grossMinutes, company);
        int paidMinutes = grossMinutes - lunchMinutes;
        int overtimeMinutes = status == DayStatus.INCOMPLETE ? 0 : Math.max(0, paidMinutes - scheduledMinutes);

        return new WorkDay(date, status, paidMinutes, overtimeMinutes, lunchMinutes,
                toLocalTime(firstIn, zone), toLocalTime(lastOut, zone), List.copyOf(sites));
    }

    private static DayStatus statusWithoutEvents(LocalDate date, LocalDate today, int scheduledMinutes) {
        if (scheduledMinutes == 0) {
            return DayStatus.OFF;
        }
        // Today isn't "absent" yet: the day isn't over
        return date.isBefore(today) ? DayStatus.ABSENT : DayStatus.UPCOMING;
    }

    // Logged lunch: take off exactly what was taken (a lunch cut short on site costs less).
    // No lunch logged on a full day: the standard lunch is still unpaid, so skipping it
    // can't quietly turn into overtime.
    private static int lunchMinutes(boolean lunchLogged, long lunchSeconds, int grossMinutes, Company company) {
        int lunch;
        if (lunchLogged) {
            lunch = (int) (lunchSeconds / 60);
        } else if (grossMinutes > Company.LUNCH_AFTER_MINUTES) {
            lunch = company.getLunchMinutes();
        } else {
            lunch = 0;
        }
        return Math.min(lunch, grossMinutes);
    }

    private static long secondsBetween(Instant from, Instant to) {
        return Math.max(0, Duration.between(from, to).getSeconds());
    }

    private static LocalTime toLocalTime(Instant instant, ZoneId zone) {
        return instant == null ? null : LocalTime.ofInstant(instant, zone).truncatedTo(ChronoUnit.MINUTES);
    }
}
