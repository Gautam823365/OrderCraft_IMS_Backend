package com.boot.ordercraft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.boot.ordercraft.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
   
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByCategory_CategoryId(Long categoryId);
    List<Product> findByProductNameContainingIgnoreCaseAndCategory_CategoryId(String productName, Long categoryId);
    Long countByDemandedQuantityGreaterThan(Long value);
    List<Product> findByDemandedQuantityGreaterThan(Long quantity);
    @Query("SELECT p FROM Product p WHERE p.productQuantity < p.threshold")
    List<Product> findByProductQuantityLessThanThreshold();
}
