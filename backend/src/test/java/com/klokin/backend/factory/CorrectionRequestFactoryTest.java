package com.klokin.backend.factory;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.CorrectionStatus;
import com.klokin.backend.domain.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CorrectionRequestFactoryTest {

    private final Company company = CompanyFactory.create("Demo Signs", null, null, null, null, null, null);
    private final Employee lee = EmployeeFactory.create(company, "Lee", "Jacobs", null, null, null);
    private final LocalDate lastWeek = LocalDate.now().minusDays(7);

    @Test
    void create_forgotToClockOut_isPending() {
        assertEquals(CorrectionStatus.PENDING, CorrectionRequestFactory
                .create(lee, lastWeek, null, LocalTime.of(17, 0), "Forgot to clock out").getStatus());
    }

    @Test
    void create_withoutAnyTime_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> CorrectionRequestFactory.create(lee, lastWeek, null, null, "Forgot"));
    }

    @Test
    void create_forTomorrow_throws() {
        assertThrows(IllegalArgumentException.class, () -> CorrectionRequestFactory
                .create(lee, LocalDate.now().plusDays(1), LocalTime.of(8, 0), null, "Early start"));
    }
}
