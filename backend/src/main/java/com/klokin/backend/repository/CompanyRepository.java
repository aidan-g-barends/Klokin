package com.klokin.backend.repository;

import com.klokin.backend.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data writes the SQL for us: save, findById, findAll, deleteById and more come for free
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
