package com.klokin.backend.service;

import com.klokin.backend.domain.Company;
import com.klokin.backend.exception.NotFoundException;
import com.klokin.backend.factory.CompanyFactory;
import com.klokin.backend.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Mockito replaces the real repository with a fake, so these tests need no database
@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository repository;

    @InjectMocks
    private CompanyServiceImpl service;

    @Test
    void create_savesCompany() {
        Company company = CompanyFactory.create("Demo Signs", null, null, null, null, null, null);
        when(repository.save(company)).thenReturn(company);

        assertEquals(company, service.create(company));
        verify(repository).save(company);
    }

    @Test
    void read_missingCompany_throwsNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.read(99L));
    }
}
