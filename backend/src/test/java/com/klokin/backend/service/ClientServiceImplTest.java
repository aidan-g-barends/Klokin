package com.klokin.backend.service;

import com.klokin.backend.domain.Client;
import com.klokin.backend.domain.Company;
import com.klokin.backend.factory.ClientFactory;
import com.klokin.backend.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientServiceImpl service;

    @Test
    void findOrCreate_existingName_returnsExistingWithoutSaving() {
        Company company = new Company.Builder().setId(1L).setName("Demo Signs").build();
        Client existing = new Client.Builder().copy(ClientFactory.create(company, "Spar Langebaan", null)).setId(7L).build();
        when(repository.findByCompany_IdAndNameIgnoreCase(1L, "spar langebaan")).thenReturn(Optional.of(existing));

        Client result = service.findOrCreate(ClientFactory.create(company, "spar langebaan", null));

        assertEquals(7L, result.getId());
        verify(repository, never()).save(any(Client.class));
    }
}
