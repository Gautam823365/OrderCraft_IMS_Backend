package com.boot.ordercraft.repository;

import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.model.ProductDemand;
import com.boot.ordercraft.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDemandRepository extends JpaRepository<ProductDemand, Long> {
	    long countByDemandStatusNot(String status);

}
