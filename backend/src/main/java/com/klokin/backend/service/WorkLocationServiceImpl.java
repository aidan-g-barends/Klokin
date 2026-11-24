package com.klokin.backend.service;

import com.klokin.backend.domain.WorkLocation;
import com.klokin.backend.exception.NotFoundException;
import com.klokin.backend.repository.WorkLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkLocationServiceImpl implements WorkLocationService {

    private final WorkLocationRepository repository;

    public WorkLocationServiceImpl(WorkLocationRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkLocation create(WorkLocation location) {
        return repository.save(location);
    }

    @Override
    public WorkLocation read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Work location " + id + " not found"));
    }

    @Override
    public List<WorkLocation> getByCompany(Long companyId) {
        return repository.findByCompany_IdOrderByNameAsc(companyId);
    }
}
