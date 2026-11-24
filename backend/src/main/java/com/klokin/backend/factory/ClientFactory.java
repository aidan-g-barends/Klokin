package com.klokin.backend.factory;

import com.klokin.backend.domain.Client;
import com.klokin.backend.domain.Company;
import com.klokin.backend.util.Helper;

public final class ClientFactory {

    private ClientFactory() {
    }

    public static Client create(Company company, String name, String address) {
        Helper.requireNotNull(company, "Company");
        Helper.requireText(name, "Client name");

        return new Client.Builder()
                .setCompany(company)
                .setName(name.trim())
                .setAddress(Helper.isNullOrEmpty(address) ? null : address.trim())
                .build();
    }
}
