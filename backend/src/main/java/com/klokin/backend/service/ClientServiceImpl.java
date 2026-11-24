package com.klokin.backend.service;

import com.klokin.backend.domain.Client;
import com.klokin.backend.exception.NotFoundException;
import com.klokin.backend.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    public ClientServiceImpl(ClientRepository repository) {
        this.repository = repository;
    }

    // Two installers adding "Spar Langebaan" on the same day should end up with one client, not two
    @Override
    public Client findOrCreate(Client client) {
        return repository.findByCompany_IdAndNameIgnoreCase(client.getCompany().getId(), client.getName())
                .orElseGet(() -> repository.save(client));
    }

    @Override
    public Client read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client " + id + " not found"));
    }

    @Override
    public List<Client> getByCompany(Long companyId) {
        return repository.findByCompany_IdOrderByNameAsc(companyId);
    }
}
