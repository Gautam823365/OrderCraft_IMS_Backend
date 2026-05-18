package com.boot.ordercraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.ordercraft.model.DeliveryTracking;
import com.boot.ordercraft.model.PurchaseOrder;

public interface DeliveryRepository extends JpaRepository<DeliveryTracking, Long> {
	DeliveryTracking findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
