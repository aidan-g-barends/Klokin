package com.klokin.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// A customer whose premises staff work at (e.g. installing a shopfront sign).
// Sites change daily, so installers can add a new client straight from the phone.
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String name;

    private String address;

    protected Client() {
    }

    private Client(Builder builder) {
        this.id = builder.id;
        this.company = builder.company;
        this.name = builder.name;
        this.address = builder.address;
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Client{id=" + id + ", name='" + name + "'}";
    }

    public static class Builder {
        private Long id;
        private Company company;
        private String name;
        private String address;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setCompany(Company company) {
            this.company = company;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder copy(Client client) {
            this.id = client.id;
            this.company = client.company;
            this.name = client.name;
            this.address = client.address;
            return this;
        }

        public Client build() {
            return new Client(this);
        }
    }
}
