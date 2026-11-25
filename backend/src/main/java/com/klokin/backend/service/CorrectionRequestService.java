package com.klokin.backend.service;

import com.klokin.backend.domain.CorrectionRequest;

import java.util.List;

public interface CorrectionRequestService {

    CorrectionRequest create(CorrectionRequest request);

    CorrectionRequest read(Long id);

    List<CorrectionRequest> getByEmployee(Long employeeId);

    List<CorrectionRequest> getPendingForCompany(Long companyId);

    CorrectionRequest approve(Long id);

    CorrectionRequest reject(Long id);
}
