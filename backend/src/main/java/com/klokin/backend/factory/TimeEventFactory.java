package com.klokin.backend.factory;

import com.klokin.backend.domain.Client;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.EventSource;
import com.klokin.backend.domain.EventType;
import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.domain.WorkLocation;
import com.klokin.backend.util.Helper;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class TimeEventFactory {

    public static final String FLAG_NO_LOCATION = "NO_LOCATION";
    public static final String FLAG_FUTURE_TIME = "FUTURE_TIME";
    public static final String FLAG_AWAY_FROM_WORKPLACE = "AWAY_FROM_WORKPLACE";

    // Phone clocks drift a little; more than this ahead of the server means the time was changed
    private static final Duration ALLOWED_CLOCK_DRIFT = Duration.ofMinutes(5);
    private static final int MAX_NOTE_LENGTH = 500;

    private TimeEventFactory() {
    }

    // Validates one event from the phone and works out its flags.
    // workLocations = the company's fixed places, used to spot clock-ins away from work.
    public static TimeEvent create(String eventUuid, Employee employee, EventType type, EventSource source,
                                   Instant occurredAt, Double latitude, Double longitude,
                                   Client client, String note, List<WorkLocation> workLocations) {
        Helper.requireText(eventUuid, "Event id");
        String uuid = eventUuid.trim().toLowerCase();
        if (!isUuid(uuid)) {
            throw new IllegalArgumentException("Event id must be a UUID");
        }
        Helper.requireNotNull(employee, "Employee");
        if (!employee.isActive()) {
            throw new IllegalArgumentException("Employee is not active");
        }
        Helper.requireNotNull(type, "Event type");
        Helper.requireNotNull(source, "Event source");
        Helper.requireNotNull(occurredAt, "Event time");

        // Location is all or nothing: both coordinates, or neither (location switched off)
        boolean hasLocation = latitude != null && longitude != null;
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException("Give both latitude and longitude, or neither");
        }
        if (hasLocation && (!Helper.isValidLatitude(latitude) || !Helper.isValidLongitude(longitude))) {
            throw new IllegalArgumentException("Location is out of range");
        }

        if (type == EventType.SITE_ARRIVE && client == null) {
            throw new IllegalArgumentException("Arriving on site needs a client");
        }
        // Stops one company's staff logging time against another company's client
        if (client != null && !Objects.equals(client.getCompany().getId(), employee.getCompany().getId())) {
            throw new IllegalArgumentException("Client belongs to a different company");
        }

        if (note != null && note.length() > MAX_NOTE_LENGTH) {
            throw new IllegalArgumentException("Note can be at most " + MAX_NOTE_LENGTH + " characters");
        }

        Instant receivedAt = Instant.now();
        List<String> flags = flagsFor(type, occurredAt, receivedAt, hasLocation, latitude, longitude,
                client, workLocations);

        return new TimeEvent.Builder()
                .setEventUuid(uuid)
                .setEmployee(employee)
                .setType(type)
                .setSource(source)
                .setOccurredAt(occurredAt)
                .setReceivedAt(receivedAt)
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setClient(client)
                .setNote(Helper.isNullOrEmpty(note) ? null : note.trim())
                .setFlags(flags.isEmpty() ? null : String.join(",", flags))
                .build();
    }

    // Flags don't block an event (the work really may have happened); they just tell the boss to look
    private static List<String> flagsFor(EventType type, Instant occurredAt, Instant receivedAt,
                                         boolean hasLocation, Double latitude, Double longitude,
                                         Client client, List<WorkLocation> workLocations) {
        List<String> flags = new ArrayList<>();

        boolean needsLocation = type == EventType.CLOCK_IN || type == EventType.CLOCK_OUT
                || type == EventType.SITE_ARRIVE;
        if (needsLocation && !hasLocation) {
            flags.add(FLAG_NO_LOCATION);
        }

        if (occurredAt.isAfter(receivedAt.plus(ALLOWED_CLOCK_DRIFT))) {
            flags.add(FLAG_FUTURE_TIME);
        }

        // Clocking in somewhere that isn't the shop and isn't a client site
        boolean hasWorkplaces = workLocations != null && !workLocations.isEmpty();
        if (type == EventType.CLOCK_IN && hasLocation && client == null && hasWorkplaces
                && workLocations.stream().noneMatch(place -> place.contains(latitude, longitude))) {
            flags.add(FLAG_AWAY_FROM_WORKPLACE);
        }

        return flags;
    }

    private static boolean isUuid(String value) {
        try {
            UUID.fromString(value);
            return value.length() == 36;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
