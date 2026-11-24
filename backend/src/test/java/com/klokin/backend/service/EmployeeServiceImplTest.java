package com.klokin.backend.service;

import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.factory.CompanyFactory;
import com.klokin.backend.factory.EmployeeFactory;
import com.klokin.backend.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeServiceImpl service;

    @Test
    void deactivate_keepsEmployeeButMarksInactive() {
        Company company = CompanyFactory.create("Demo Signs", null, null, null, null, null, null);
        Employee lee = new Employee.Builder()
                .copy(EmployeeFactory.create(company, "Lee", "Jacobs", null, null, null))
                .setId(1L)
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(lee));
        // Return whatever gets saved, so we can check what the service built
        when(repository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee result = service.deactivate(1L);

        assertFalse(result.isActive());
    }
}
