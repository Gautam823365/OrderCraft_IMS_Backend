package com.boot.ordercraft.controller;

import com.boot.ordercraft.model.ReturnOrder;
import com.boot.ordercraft.service.ReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class ReturnOrderController {

    @Autowired
    private ReturnOrderService returnOrderService;

//    @PostMapping("/returnorder")
//    public ReturnOrder createReturnOrder(@RequestBody ReturnOrder returnOrder) {
//        return returnOrderService.createReturnOrder(returnOrder);
//    }
    
    @PostMapping("/returnorder")
    public ResponseEntity<?> createReturnOrder(@RequestBody ReturnOrder returnOrder) {
        try {
            ReturnOrder savedReturnOrder = returnOrderService.createReturnOrder(returnOrder);
            return ResponseEntity.ok(savedReturnOrder);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create return order");
        }
    }


    @GetMapping("/inventory/getallreturnedorders")
    public List<ReturnOrder> getAllReturnOrders() {
        return returnOrderService.getAllReturnOrders();
    }

    @GetMapping("returnorder/{id}")
    public ReturnOrder getReturnOrderById(@PathVariable Long id) {
        return returnOrderService.getReturnOrderById(id);
    }

    @GetMapping("returnorder/filter")
    public List<ReturnOrder> filterReturnOrders(
            @RequestParam(required = false) Long returnId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date rdate,
            @RequestParam(required = false) Long purchaseOrderId
    ) {
        return returnOrderService.filterReturnOrders(returnId, rdate, purchaseOrderId);
    }
    
    @PutMapping("/inventory/approveReturnOrder/{id}")
    public ResponseEntity<?> approveReturnOrder(@PathVariable Long id) {
        try {
            ReturnOrder approvedOrder = returnOrderService.approveReturnOrder(id);
            return ResponseEntity.ok(approvedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Approval failed");
        }
    }

}
