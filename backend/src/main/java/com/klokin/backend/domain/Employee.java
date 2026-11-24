package com.klokin.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many employees belong to one company. LAZY means Hibernate only loads the company
    // from the database when we actually use it, not every time we load an employee.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    private String phone;

    // Stored as text ("MANAGER") instead of a number, so reordering the enum can't corrupt data
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // We deactivate instead of deleting: payroll and labour records must keep their history
    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Employee() {
    }

    private Employee(Builder builder) {
        this.id = builder.id;
        this.company = builder.company;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.role = builder.role;
        this.active = builder.active;
        this.createdAt = builder.createdAt;
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + getFullName() + "', role=" + role + ", active=" + active + "}";
    }

    public static class Builder {
        private Long id;
        private Company company;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private Role role;
        private boolean active;
        private Instant createdAt;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setCompany(Company company) {
            this.company = company;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder copy(Employee employee) {
            this.id = employee.id;
            this.company = employee.company;
            this.firstName = employee.firstName;
            this.lastName = employee.lastName;
            this.email = employee.email;
            this.phone = employee.phone;
            this.role = employee.role;
            this.active = employee.active;
            this.createdAt = employee.createdAt;
            return this;
        }

        public Employee build() {
            return new Employee(this);
        }
    }
}
