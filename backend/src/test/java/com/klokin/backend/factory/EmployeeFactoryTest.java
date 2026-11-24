package com.klokin.backend.factory;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeFactoryTest {

    private final Company company = CompanyFactory.create("Demo Signs", null, null, null, null, null, null);

    @Test
    void create_cleansPhoneAndDefaultsRole() {
        Employee employee = EmployeeFactory.create(company, "Lee", "Jacobs", null, "082 123 4567", null);

        assertEquals("0821234567", employee.getPhone());
        assertEquals(Role.EMPLOYEE, employee.getRole());
        assertTrue(employee.isActive());
    }

    @Test
    void create_withInvalidPhone_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> EmployeeFactory.create(company, "Lee", "Jacobs", null, "12345", null));
    }

    @Test
    void create_withoutCompany_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> EmployeeFactory.create(null, "Lee", "Jacobs", null, null, null));
    }
}
