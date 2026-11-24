package com.klokin.backend.service;

import com.klokin.backend.domain.Company;
import com.klokin.backend.exception.NotFoundException;
import com.klokin.backend.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository repository;

    // Constructor injection: Spring passes in the repository, and the field can be final
    public CompanyServiceImpl(CompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Company create(Company company) {
        return repository.save(company);
    }

    @Override
    public Company read(Long id) {
        // orElseThrow: if the Optional is empty, throw our 404 exception
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company " + id + " not found"));
    }

    @Override
    public Company update(Long id, Company changes) {
        Company existing = read(id);
        // Take the new values from the (already validated) changes, but keep the id and
        // creation date from the database so a caller can't overwrite them
        Company updated = new Company.Builder()
                .copy(changes)
                .setId(existing.getId())
                .setCreatedAt(existing.getCreatedAt())
                .build();
        return repository.save(updated);
    }

    @Override
    public List<Company> getAll() {
        return repository.findAll();
    }
}
