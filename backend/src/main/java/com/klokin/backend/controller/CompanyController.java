package com.klokin.backend.controller;

import com.klokin.backend.dto.CompanyDto;
import com.klokin.backend.service.CompanyService;
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

// REST style: the URL names the thing (/api/companies), the HTTP method says the action
// (POST = create, GET = read, PUT = update)
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    // POST /api/companies -> 201 Created with the saved company
    @PostMapping
    public ResponseEntity<CompanyDto.Response> create(@RequestBody CompanyDto.Request request) {
        CompanyDto.Response body = CompanyDto.Response.from(service.create(request.toCompany()));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    public CompanyDto.Response read(@PathVariable Long id) {
        return CompanyDto.Response.from(service.read(id));
    }

    @PutMapping("/{id}")
    public CompanyDto.Response update(@PathVariable Long id, @RequestBody CompanyDto.Request request) {
        return CompanyDto.Response.from(service.update(id, request.toCompany()));
    }

    @GetMapping
    public List<CompanyDto.Response> getAll() {
        return service.getAll().stream().map(CompanyDto.Response::from).toList();
    }
}
