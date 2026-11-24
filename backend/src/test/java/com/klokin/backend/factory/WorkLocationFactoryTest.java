package com.klokin.backend.factory;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.WorkLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkLocationFactoryTest {

    private final Company company = CompanyFactory.create("Demo Signs", null, null, null, null, null, null);

    @Test
    void contains_trueNearbyFalseFarAway() {
        WorkLocation shop = WorkLocationFactory.create(company, "Shop", -33.0930, 18.0340, 100, null);

        assertTrue(shop.contains(-33.0933, 18.0342));   // about 40 m away
        assertFalse(shop.contains(-33.0100, 17.9400));  // a different town
    }

    @Test
    void create_withInvalidLatitude_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> WorkLocationFactory.create(company, "Shop", -120.0, 18.0340, 100, null));
    }

    @Test
    void create_withTinyRadius_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> WorkLocationFactory.create(company, "Shop", -33.0930, 18.0340, 5, null));
    }
}
