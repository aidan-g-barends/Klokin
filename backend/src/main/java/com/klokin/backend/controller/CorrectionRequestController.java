package com.klokin.backend.controller;

import com.klokin.backend.domain.Employee;
import com.klokin.backend.dto.CorrectionRequestDto;
import com.klokin.backend.service.CorrectionRequestService;
import com.klokin.backend.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CorrectionRequestController {

    private final CorrectionRequestService correctionService;
    private final EmployeeService employeeService;

    public CorrectionRequestController(CorrectionRequestService correctionService,
                                       EmployeeService employeeService) {
        this.correctionService = correctionService;
        this.employeeService = employeeService;
    }

    // Employee taps "My hours are wrong"
    @PostMapping("/employees/{employeeId}/corrections")
    public ResponseEntity<CorrectionRequestDto.Response> create(@PathVariable Long employeeId,
                                                                @RequestBody CorrectionRequestDto.Request request) {
        Employee employee = employeeService.read(employeeId);
        CorrectionRequestDto.Response body =
                CorrectionRequestDto.Response.from(correctionService.create(request.toCorrection(employee)));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/employees/{employeeId}/corrections")
    public List<CorrectionRequestDto.Response> getByEmployee(@PathVariable Long employeeId) {
        return correctionService.getByEmployee(employeeId).stream().map(CorrectionRequestDto.Response::from).toList();
    }

    // Boss inbox: everything waiting for a decision
    @GetMapping("/companies/{companyId}/corrections/pending")
    public List<CorrectionRequestDto.Response> getPending(@PathVariable Long companyId) {
        return correctionService.getPendingForCompany(companyId).stream()
                .map(CorrectionRequestDto.Response::from).toList();
    }

    @PostMapping("/corrections/{id}/approve")
    public CorrectionRequestDto.Response approve(@PathVariable Long id) {
        return CorrectionRequestDto.Response.from(correctionService.approve(id));
    }

    @PostMapping("/corrections/{id}/reject")
    public CorrectionRequestDto.Response reject(@PathVariable Long id) {
        return CorrectionRequestDto.Response.from(correctionService.reject(id));
    }
}
