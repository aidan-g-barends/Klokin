package com.klokin.backend.repository;

import com.klokin.backend.domain.CorrectionRequest;
import com.klokin.backend.domain.CorrectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorrectionRequestRepository extends JpaRepository<CorrectionRequest, Long> {

    List<CorrectionRequest> findByEmployee_IdOrderByCreatedAtDesc(Long employeeId);

    // employee.company.id = ? and status = ?, oldest first so the boss works through them in order
    List<CorrectionRequest> findByEmployee_Company_IdAndStatusOrderByCreatedAtAsc(Long companyId,
                                                                                  CorrectionStatus status);
}
