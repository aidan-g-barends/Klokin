package com.klokin.backend.service;

import com.klokin.backend.domain.Client;

import java.util.List;

public interface ClientService {

    // Returns the existing client if one with the same name exists, otherwise saves the new one
    Client findOrCreate(Client client);

    Client read(Long id);

    List<Client> getByCompany(Long companyId);
}
