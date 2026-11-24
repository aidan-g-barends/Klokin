package com.klokin.backend.factory;

import com.klokin.backend.domain.Company;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompanyFactoryTest {

    @Test
    void create_withOnlyName_usesSouthAfricanDefaults() {
        Company company = CompanyFactory.create("Demo Signs", null, null, null, null, null, null);

        assertEquals("Africa/Johannesburg", company.getTimeZone());
        assertEquals(480, company.scheduledMinutesOn(DayOfWeek.MONDAY));
        assertEquals(0, company.scheduledMinutesOn(DayOfWeek.SATURDAY));
    }

    @Test
    void create_withSaturdayHours_countsSaturdayWithoutLunch() {
        Company company = CompanyFactory.create("Demo Signs", null, null, null,
                LocalTime.of(9, 0), LocalTime.of(13, 0), null);

        assertEquals(240, company.scheduledMinutesOn(DayOfWeek.SATURDAY));
    }

    @Test
    void create_withBlankName_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> CompanyFactory.create(" ", null, null, null, null, null, null));
    }

    @Test
    void create_withOnlySaturdayStart_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> CompanyFactory.create("Demo Signs", null, null, null, LocalTime.of(9, 0), null, null));
    }
}
