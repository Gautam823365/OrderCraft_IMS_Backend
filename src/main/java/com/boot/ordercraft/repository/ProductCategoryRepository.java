package com.boot.ordercraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.ordercraft.model.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
   
}
