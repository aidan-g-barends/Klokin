package com.klokin.backend.repository;

import com.klokin.backend.domain.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkLocationRepository extends JpaRepository<WorkLocation, Long> {

    List<WorkLocation> findByCompany_IdOrderByNameAsc(Long companyId);
}
