package com.boot.ordercraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.ordercraft.model.ReturnOrder;

public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long> {

}
