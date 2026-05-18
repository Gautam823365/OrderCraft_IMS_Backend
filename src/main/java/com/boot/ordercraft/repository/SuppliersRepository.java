package com.boot.ordercraft.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.ordercraft.model.Suppliers;

public interface SuppliersRepository extends JpaRepository<Suppliers, Long> {
	Optional<Suppliers> findByContactEmail(String contactEmail);
    Optional<Suppliers> findByPhone(String phone);
}
