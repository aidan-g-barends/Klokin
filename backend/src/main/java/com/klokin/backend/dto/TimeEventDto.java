package com.klokin.backend.dto;

import com.klokin.backend.domain.EventSource;
import com.klokin.backend.domain.EventType;
import com.klokin.backend.domain.TimeEvent;

import java.time.Instant;
import java.util.List;

public final class TimeEventDto {

    private TimeEventDto() {
    }

    // What the phone sends when it syncs: everything it saved while offline, in one go
    public record SyncRequest(Long employeeId, List<SyncEvent> events) {
    }

    public record SyncEvent(String eventUuid,
                            EventType type,
                            EventSource source,
                            Instant occurredAt,
                            Double latitude,
                            Double longitude,
                            Long clientId,
                            String note) {
    }

    public enum SyncStatus {
        SAVED,      // new event, stored
        DUPLICATE,  // we already had it, so the phone can mark it as synced
        REJECTED    // invalid; message says why
    }

    // One result per event, so the phone knows exactly which ones it can mark as synced
    public record SyncResult(String eventUuid, SyncStatus status, String message) {
    }

    public record SyncResponse(int saved, int duplicates, int rejected, List<SyncResult> results) {

        public static SyncResponse of(List<SyncResult> results) {
            return new SyncResponse(
                    count(results, SyncStatus.SAVED),
                    count(results, SyncStatus.DUPLICATE),
                    count(results, SyncStatus.REJECTED),
                    List.copyOf(results));
        }

        private static int count(List<SyncResult> results, SyncStatus status) {
            return (int) results.stream().filter(result -> result.status() == status).count();
        }
    }

    // One line in the boss's live feed: "07:52 Lee Jacobs CLOCK_IN (AUTO_WIFI)"
    public record FeedItem(Long eventId,
                           Long employeeId,
                           String employeeName,
                           EventType type,
                           EventSource source,
                           Instant occurredAt,
                           Double latitude,
                           Double longitude,
                           String clientName,
                           String note,
                           String flags) {

        public static FeedItem from(TimeEvent event) {
            return new FeedItem(event.getId(), event.getEmployee().getId(), event.getEmployee().getFullName(),
                    event.getType(), event.getSource(), event.getOccurredAt(),
                    event.getLatitude(), event.getLongitude(),
                    event.getClient() != null ? event.getClient().getName() : null,
                    event.getNote(), event.getFlags());
        }
    }
}
