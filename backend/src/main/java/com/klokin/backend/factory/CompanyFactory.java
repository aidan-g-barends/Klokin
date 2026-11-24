package com.klokin.backend.factory;

import com.klokin.backend.domain.Company;
import com.klokin.backend.util.Helper;

import java.time.Instant;
import java.time.LocalTime;

// The only way to make a valid new Company. It throws IllegalArgumentException with a clear
// message instead of returning null, so the API can tell the caller exactly what's wrong.
public final class CompanyFactory {

    public static final String DEFAULT_TIME_ZONE = "Africa/Johannesburg";
    public static final LocalTime DEFAULT_WEEKDAY_START = LocalTime.of(8, 0);
    public static final LocalTime DEFAULT_WEEKDAY_END = LocalTime.of(17, 0);
    public static final int DEFAULT_LUNCH_MINUTES = 60;

    private CompanyFactory() {
    }

    // Anything left null falls back to a sensible SA default (except Saturday: null = no Saturday work)
    public static Company create(String name, String timeZone,
                                 LocalTime weekdayStart, LocalTime weekdayEnd,
                                 LocalTime saturdayStart, LocalTime saturdayEnd,
                                 Integer lunchMinutes) {
        Helper.requireText(name, "Company name");

        String zone = Helper.isNullOrEmpty(timeZone) ? DEFAULT_TIME_ZONE : timeZone.trim();
        if (!Helper.isValidTimeZone(zone)) {
            throw new IllegalArgumentException("Unknown time zone: " + zone);
        }

        LocalTime start = weekdayStart != null ? weekdayStart : DEFAULT_WEEKDAY_START;
        LocalTime end = weekdayEnd != null ? weekdayEnd : DEFAULT_WEEKDAY_END;
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Weekday start must be before weekday end");
        }

        // Saturday hours are all or nothing: both times, or neither
        if ((saturdayStart == null) != (saturdayEnd == null)) {
            throw new IllegalArgumentException("Give both Saturday start and end, or neither");
        }
        if (saturdayStart != null && !saturdayStart.isBefore(saturdayEnd)) {
            throw new IllegalArgumentException("Saturday start must be before Saturday end");
        }

        int lunch = lunchMinutes != null ? lunchMinutes : DEFAULT_LUNCH_MINUTES;
        if (lunch < 0 || lunch > 120) {
            throw new IllegalArgumentException("Lunch must be between 0 and 120 minutes");
        }

        return new Company.Builder()
                .setName(name.trim())
                .setTimeZone(zone)
                .setWeekdayStart(start)
                .setWeekdayEnd(end)
                .setSaturdayStart(saturdayStart)
                .setSaturdayEnd(saturdayEnd)
                .setLunchMinutes(lunch)
                .setCreatedAt(Instant.now())
                .build();
    }
}
