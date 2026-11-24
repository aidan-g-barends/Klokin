package com.klokin.backend.factory;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.Role;
import com.klokin.backend.util.Helper;

import java.time.Instant;

public final class EmployeeFactory {

    private EmployeeFactory() {
    }

    // Email and phone are optional, but if given they must be valid. Role defaults to EMPLOYEE.
    public static Employee create(Company company, String firstName, String lastName,
                                  String email, String phone, Role role) {
        Helper.requireNotNull(company, "Company");
        Helper.requireText(firstName, "First name");
        Helper.requireText(lastName, "Last name");

        String cleanEmail = Helper.isNullOrEmpty(email) ? null : email.trim().toLowerCase();
        if (cleanEmail != null && !Helper.isValidEmail(cleanEmail)) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }

        String cleanPhone = Helper.isNullOrEmpty(phone) ? null : Helper.normalisePhone(phone);
        if (cleanPhone != null && !Helper.isValidSaPhone(cleanPhone)) {
            throw new IllegalArgumentException("Invalid South African phone number: " + phone);
        }

        return new Employee.Builder()
                .setCompany(company)
                .setFirstName(firstName.trim())
                .setLastName(lastName.trim())
                .setEmail(cleanEmail)
                .setPhone(cleanPhone)
                .setRole(role != null ? role : Role.EMPLOYEE)
                .setActive(true)
                .setCreatedAt(Instant.now())
                .build();
    }
}
