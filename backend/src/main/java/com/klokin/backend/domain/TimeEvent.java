package com.klokin.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

// One raw thing that happened: "Lee clocked in at 07:56 at the shop, by Wi-Fi".
// Events are never edited; hours are always calculated from them, so they are the single
// source of truth. Wrong hours get fixed with correction requests, which keeps a paper trail.
@Entity
@Table(name = "time_events",
        // Speeds up the most common query: one employee's events in a date range
        indexes = @Index(name = "idx_time_events_employee_time", columnList = "employee_id, occurred_at"))
public class TimeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Made by the phone before it syncs. If the phone sends the same event twice
    // (connection dropped mid-sync), this id lets us save it only once.
    @Column(nullable = false, unique = true, length = 36)
    private String eventUuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventSource source;

    // When it happened on the phone (can be hours before it reached us if the phone was offline)
    @Column(nullable = false)
    private Instant occurredAt;

    // When the server received it
    @Column(nullable = false)
    private Instant receivedAt;

    // Null when location was switched off
    private Double latitude;

    private Double longitude;

    // Only for site events: which client's premises
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // Optional reason, e.g. "Lunch cut short, client needed us"
    @Column(length = 500)
    private String note;

    // Comma-separated warnings for the boss, e.g. "NO_LOCATION"; null when all is fine
    private String flags;

    protected TimeEvent() {
    }

    private TimeEvent(Builder builder) {
        this.id = builder.id;
        this.eventUuid = builder.eventUuid;
        this.employee = builder.employee;
        this.type = builder.type;
        this.source = builder.source;
        this.occurredAt = builder.occurredAt;
        this.receivedAt = builder.receivedAt;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.client = builder.client;
        this.note = builder.note;
        this.flags = builder.flags;
    }

    public Long getId() {
        return id;
    }

    public String getEventUuid() {
        return eventUuid;
    }

    public Employee getEmployee() {
        return employee;
    }

    public EventType getType() {
        return type;
    }

    public EventSource getSource() {
        return source;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Client getClient() {
        return client;
    }

    public String getNote() {
        return note;
    }

    public String getFlags() {
        return flags;
    }

    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    @Override
    public String toString() {
        return "TimeEvent{id=" + id + ", type=" + type + ", source=" + source + ", occurredAt=" + occurredAt + "}";
    }

    public static class Builder {
        private Long id;
        private String eventUuid;
        private Employee employee;
        private EventType type;
        private EventSource source;
        private Instant occurredAt;
        private Instant receivedAt;
        private Double latitude;
        private Double longitude;
        private Client client;
        private String note;
        private String flags;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setEventUuid(String eventUuid) {
            this.eventUuid = eventUuid;
            return this;
        }

        public Builder setEmployee(Employee employee) {
            this.employee = employee;
            return this;
        }

        public Builder setType(EventType type) {
            this.type = type;
            return this;
        }

        public Builder setSource(EventSource source) {
            this.source = source;
            return this;
        }

        public Builder setOccurredAt(Instant occurredAt) {
            this.occurredAt = occurredAt;
            return this;
        }

        public Builder setReceivedAt(Instant receivedAt) {
            this.receivedAt = receivedAt;
            return this;
        }

        public Builder setLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setClient(Client client) {
            this.client = client;
            return this;
        }

        public Builder setNote(String note) {
            this.note = note;
            return this;
        }

        public Builder setFlags(String flags) {
            this.flags = flags;
            return this;
        }

        public Builder copy(TimeEvent event) {
            this.id = event.id;
            this.eventUuid = event.eventUuid;
            this.employee = event.employee;
            this.type = event.type;
            this.source = event.source;
            this.occurredAt = event.occurredAt;
            this.receivedAt = event.receivedAt;
            this.latitude = event.latitude;
            this.longitude = event.longitude;
            this.client = event.client;
            this.note = event.note;
            this.flags = event.flags;
            return this;
        }

        public TimeEvent build() {
            return new TimeEvent(this);
        }
    }
}
