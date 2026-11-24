package com.klokin.backend.dto;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.Role;
import com.klokin.backend.factory.EmployeeFactory;

public final class EmployeeDto {

    private EmployeeDto() {
    }

    public record Request(String firstName, String lastName, String email, String phone, Role role) {

        // The company comes from the URL (/api/companies/{companyId}/employees), not the body
        public Employee toEmployee(Company company) {
            return EmployeeFactory.create(company, firstName, lastName, email, phone, role);
        }
    }

    public record Response(Long id,
                           Long companyId,
                           String firstName,
                           String lastName,
                           String fullName,
                           String email,
                           String phone,
                           Role role,
                           boolean active) {

        public static Response from(Employee employee) {
            return new Response(employee.getId(), employee.getCompany().getId(),
                    employee.getFirstName(), employee.getLastName(), employee.getFullName(),
                    employee.getEmail(), employee.getPhone(), employee.getRole(), employee.isActive());
        }
    }
}
