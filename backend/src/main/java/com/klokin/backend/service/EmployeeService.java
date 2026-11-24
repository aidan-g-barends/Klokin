package com.klokin.backend.service;

import com.klokin.backend.domain.Employee;

import java.util.List;

public interface EmployeeService {

    Employee create(Employee employee);

    Employee read(Long id);

    Employee update(Long id, Employee changes);

    List<Employee> getByCompany(Long companyId);

    Employee deactivate(Long id);
}
