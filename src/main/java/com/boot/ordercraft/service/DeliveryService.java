package com.boot.ordercraft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.ordercraft.model.DeliveryTracking;
import com.boot.ordercraft.repository.DeliveryRepository;
import com.boot.ordercraft.repository.PurchaseOrderRepository;


@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepo;
    
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepo;

    public DeliveryTracking trackOrder(Long orderId) {
        return purchaseOrderRepo.findById(orderId)
                .map(order -> deliveryRepo.findByPurchaseOrder(order))
                .orElse(null);
    }

    public DeliveryTracking updateTracking(DeliveryTracking tracking) {
        return deliveryRepo.save(tracking);
    }
}
