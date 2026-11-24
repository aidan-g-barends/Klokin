package com.klokin.backend.repository;

import com.klokin.backend.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Spring Data reads the method name and writes the query:
    // "find by company.id, sorted by firstName" (the underscore means "go into company")
    List<Employee> findByCompany_IdOrderByFirstNameAsc(Long companyId);
}
