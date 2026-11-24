package com.klokin.backend.controller;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.dto.EmployeeDto;
import com.klokin.backend.service.CompanyService;
import com.klokin.backend.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CompanyService companyService;

    public EmployeeController(EmployeeService employeeService, CompanyService companyService) {
        this.employeeService = employeeService;
        this.companyService = companyService;
    }

    // Employees are created inside a company, so the URL nests them under it
    @PostMapping("/companies/{companyId}/employees")
    public ResponseEntity<EmployeeDto.Response> create(@PathVariable Long companyId,
                                                       @RequestBody EmployeeDto.Request request) {
        Company company = companyService.read(companyId);
        Employee saved = employeeService.create(request.toEmployee(company));
        return ResponseEntity.status(HttpStatus.CREATED).body(EmployeeDto.Response.from(saved));
    }

    @GetMapping("/companies/{companyId}/employees")
    public List<EmployeeDto.Response> getByCompany(@PathVariable Long companyId) {
        return employeeService.getByCompany(companyId).stream().map(EmployeeDto.Response::from).toList();
    }

    @GetMapping("/employees/{id}")
    public EmployeeDto.Response read(@PathVariable Long id) {
        return EmployeeDto.Response.from(employeeService.read(id));
    }

    @PutMapping("/employees/{id}")
    public EmployeeDto.Response update(@PathVariable Long id, @RequestBody EmployeeDto.Request request) {
        Employee existing = employeeService.read(id);
        Employee changes = request.toEmployee(existing.getCompany());
        return EmployeeDto.Response.from(employeeService.update(id, changes));
    }

    @PostMapping("/employees/{id}/deactivate")
    public EmployeeDto.Response deactivate(@PathVariable Long id) {
        return EmployeeDto.Response.from(employeeService.deactivate(id));
    }
}
