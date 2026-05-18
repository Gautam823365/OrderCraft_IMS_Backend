package com.boot.ordercraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

//import java.util.Optional;
//import org.springframework.data.jpa.repository.JpaRepository;
//import com.boot.ordercraft.model.ProductionResource;
//
//public interface ProductionResourceRepository extends JpaRepository<ProductionResource, Long> {
//    Optional<ProductionResource> findFirstByStatus(String status);
//}


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.boot.ordercraft.model.ProductionResource;

import java.util.Optional;

public interface ProductionResourceRepository extends JpaRepository<ProductionResource, Long> {

    @Query(value = "SELECT * FROM ORDERCRAFT_PRODUCRESOURCE r WHERE r.status = :status AND ROWNUM = 1", nativeQuery = true)
    Optional<ProductionResource> findFirstByStatus(@Param("status") String status);
}
