package com.klokin.backend.repository;

import com.klokin.backend.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByCompany_IdOrderByNameAsc(Long companyId);

    // Case-insensitive, so "spar langebaan" and "Spar Langebaan" count as the same client
    Optional<Client> findByCompany_IdAndNameIgnoreCase(Long companyId, String name);
}
