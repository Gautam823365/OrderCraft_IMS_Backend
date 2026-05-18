package com.boot.ordercraft.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.ordercraft.model.RawMaterial;
import com.boot.ordercraft.model.Role;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Integer> {
}
