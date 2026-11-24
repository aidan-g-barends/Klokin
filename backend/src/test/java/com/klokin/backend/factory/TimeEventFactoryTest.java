package com.klokin.backend.factory;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.EventSource;
import com.klokin.backend.domain.EventType;
import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.domain.WorkLocation;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeEventFactoryTest {

    private final Company company = CompanyFactory.create("Demo Signs", null, null, null, null, null, null);
    private final Employee lee = EmployeeFactory.create(company, "Lee", "Jacobs", null, null, null);
    private final WorkLocation shop = WorkLocationFactory.create(company, "Shop", -33.0930, 18.0340, 100, null);

    @Test
    void clockInAtTheShop_hasNoFlags() {
        TimeEvent event = clockIn(-33.0931, 18.0341, Instant.now());

        assertNull(event.getFlags());
    }

    @Test
    void clockInWithoutLocation_isFlagged() {
        TimeEvent event = clockIn(null, null, Instant.now());

        assertEquals(TimeEventFactory.FLAG_NO_LOCATION, event.getFlags());
    }

    @Test
    void clockInAwayFromShopInTheFuture_getsBothFlags() {
        TimeEvent event = clockIn(-33.0100, 17.9400, Instant.now().plus(Duration.ofHours(2)));

        assertEquals("FUTURE_TIME,AWAY_FROM_WORKPLACE", event.getFlags());
    }

    @Test
    void siteArriveWithoutClient_throws() {
        assertThrows(IllegalArgumentException.class, () -> TimeEventFactory.create(
                UUID.randomUUID().toString(), lee, EventType.SITE_ARRIVE, EventSource.MANUAL,
                Instant.now(), -33.0, 18.0, null, null, List.of(shop)));
    }

    private TimeEvent clockIn(Double latitude, Double longitude, Instant occurredAt) {
        return TimeEventFactory.create(UUID.randomUUID().toString(), lee, EventType.CLOCK_IN,
                EventSource.MANUAL, occurredAt, latitude, longitude, null, null, List.of(shop));
    }
}
