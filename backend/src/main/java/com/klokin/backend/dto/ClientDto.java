package com.klokin.backend.dto;

import com.klokin.backend.domain.Client;
import com.klokin.backend.domain.Company;
import com.klokin.backend.factory.ClientFactory;

public final class ClientDto {

    private ClientDto() {
    }

    public record Request(String name, String address) {

        public Client toClient(Company company) {
            return ClientFactory.create(company, name, address);
        }
    }

    public record Response(Long id, Long companyId, String name, String address) {

        public static Response from(Client client) {
            return new Response(client.getId(), client.getCompany().getId(), client.getName(), client.getAddress());
        }
    }
}
