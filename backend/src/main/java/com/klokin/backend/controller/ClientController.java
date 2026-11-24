package com.klokin.backend.controller;

import com.klokin.backend.domain.Company;
import com.klokin.backend.dto.ClientDto;
import com.klokin.backend.service.ClientService;
import com.klokin.backend.service.CompanyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/companies/{companyId}/clients")
public class ClientController {

    private final ClientService clientService;
    private final CompanyService companyService;

    public ClientController(ClientService clientService, CompanyService companyService) {
        this.clientService = clientService;
        this.companyService = companyService;
    }

    // "Pick or add client" from the phone: returns the existing client if the name is already there
    @PostMapping
    public ClientDto.Response findOrCreate(@PathVariable Long companyId, @RequestBody ClientDto.Request request) {
        Company company = companyService.read(companyId);
        return ClientDto.Response.from(clientService.findOrCreate(request.toClient(company)));
    }

    @GetMapping
    public List<ClientDto.Response> getByCompany(@PathVariable Long companyId) {
        return clientService.getByCompany(companyId).stream().map(ClientDto.Response::from).toList();
    }
}
