package com.boot.ordercraft.controller;

import com.boot.ordercraft.model.ProductDemand;
import com.boot.ordercraft.service.ProductDemandService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductDemandController {

    @Autowired
    private ProductDemandService productDemandService;

    @PatchMapping("/updateDemand/{productId}")
    public ResponseEntity<ProductDemand> updateDemand(
            @PathVariable Long productId,
            @RequestParam Long demandedQuantity,
            @RequestParam Long userId) {

        return productDemandService.updateDemandedQuantity(productId, demandedQuantity, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
//    @GetMapping("/getalldemandedproducts")
//    public List<ProductDemand> getAllDemands() {
//        return productDemandService.getAllDemands();
//    }
    
    @GetMapping("/getalldemandedproducts")
    public List<ProductDemand> getAllDemands() {
        return productDemandService.getAllDemands()
                .stream()
                .filter(d -> !"DELIVERED".equals(d.getDemandStatus()))
                .toList();
    }
    
    @PatchMapping("/deliverDemand/{demandId}")
    public ResponseEntity<ProductDemand> deliverDemand(@PathVariable Long demandId) {
        ProductDemand delivered = productDemandService.deliverDemandedProduct(demandId);
        return ResponseEntity.ok(delivered);
    }
    
    @GetMapping("/demanded/count")
    public long getDemandedProductsCount() {
        // count only NOT DELIVERED demands
        return productDemandService.countActiveDemands();
    }


}
