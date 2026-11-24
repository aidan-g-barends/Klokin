package com.klokin.backend.service;

import com.klokin.backend.domain.Company;

import java.util.List;

// The contract: what the rest of the app can do with companies. Controllers depend on this
// interface, not the implementation, so tests can swap in a mock.
public interface CompanyService {

    Company create(Company company);

    Company read(Long id);

    Company update(Long id, Company changes);

    List<Company> getAll();
}
