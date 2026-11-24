package com.klokin.backend.service;

import com.klokin.backend.domain.Employee;
import com.klokin.backend.exception.NotFoundException;
import com.klokin.backend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Employee create(Employee employee) {
        return repository.save(employee);
    }

    @Override
    public Employee read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee " + id + " not found"));
    }

    @Override
    public Employee update(Long id, Employee changes) {
        Employee existing = read(id);
        // Names, contact details and role can change; company, status and history stay as they are
        Employee updated = new Employee.Builder()
                .copy(changes)
                .setId(existing.getId())
                .setCompany(existing.getCompany())
                .setActive(existing.isActive())
                .setCreatedAt(existing.getCreatedAt())
                .build();
        return repository.save(updated);
    }

    @Override
    public List<Employee> getByCompany(Long companyId) {
        return repository.findByCompany_IdOrderByFirstNameAsc(companyId);
    }

    @Override
    public Employee deactivate(Long id) {
        Employee existing = read(id);
        return repository.save(new Employee.Builder().copy(existing).setActive(false).build());
    }
}
