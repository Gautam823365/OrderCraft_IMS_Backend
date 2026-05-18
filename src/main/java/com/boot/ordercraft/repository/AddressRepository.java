package com.boot.ordercraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.ordercraft.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}