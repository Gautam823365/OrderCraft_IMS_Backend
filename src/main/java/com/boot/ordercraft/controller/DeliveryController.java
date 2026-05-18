package com.boot.ordercraft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boot.ordercraft.model.DeliveryTracking;
import com.boot.ordercraft.service.DeliveryService;

@RestController
@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/api/orders")
//@PreAuthorize("hasRole('PROCUREMENT_OFFICER')")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryTrackingService;

    @GetMapping("/delivery-tracking/{orderId}")
    public ResponseEntity<DeliveryTracking> getTracking(@PathVariable Long orderId) {
        DeliveryTracking tracking = deliveryTrackingService.trackOrder(orderId);
        return tracking != null ? ResponseEntity.ok(tracking) : ResponseEntity.notFound().build();
    }
}
