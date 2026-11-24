package com.klokin.backend.controller;

import com.klokin.backend.domain.Company;
import com.klokin.backend.dto.WorkLocationDto;
import com.klokin.backend.service.CompanyService;
import com.klokin.backend.service.WorkLocationService;
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
@RequestMapping("/api/companies/{companyId}/locations")
public class WorkLocationController {

    private final WorkLocationService locationService;
    private final CompanyService companyService;

    public WorkLocationController(WorkLocationService locationService, CompanyService companyService) {
        this.locationService = locationService;
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<WorkLocationDto.Response> create(@PathVariable Long companyId,
                                                           @RequestBody WorkLocationDto.Request request) {
        Company company = companyService.read(companyId);
        WorkLocationDto.Response body =
                WorkLocationDto.Response.from(locationService.create(request.toWorkLocation(company)));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // The phone downloads these to set up its geofences (the circles it watches)
    @GetMapping
    public List<WorkLocationDto.Response> getByCompany(@PathVariable Long companyId) {
        return locationService.getByCompany(companyId).stream().map(WorkLocationDto.Response::from).toList();
    }
}
