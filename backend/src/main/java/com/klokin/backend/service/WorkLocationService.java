package com.klokin.backend.service;

import com.klokin.backend.domain.WorkLocation;

import java.util.List;

public interface WorkLocationService {

    WorkLocation create(WorkLocation location);

    WorkLocation read(Long id);

    List<WorkLocation> getByCompany(Long companyId);
}
