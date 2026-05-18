package com.boot.ordercraft.service;

import com.boot.ordercraft.model.InventoryTransactions;
import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.model.ProductDemand;
import com.boot.ordercraft.model.ProductionSchedule;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.InventoryTransactionsRepository;
import com.boot.ordercraft.repository.ProductDemandRepository;
import com.boot.ordercraft.repository.ProductRepository;
import com.boot.ordercraft.repository.ProductionScheduleRepository;
import com.boot.ordercraft.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductDemandService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDemandRepository productDemandRepository;

    @Autowired
    private InventoryTransactionsRepository inventoryTransactionsRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductionScheduleRepository scheduleRepo;

    public Optional<ProductDemand> updateDemandedQuantity(Long productId, Long demandedQuantity, Long userId) {
        return productRepository.findById(productId).map(product -> {

            User user = userRepository.findById(userId).orElse(null);

            // ✅ Save demand in ProductDemand table
            ProductDemand demand = new ProductDemand();
            demand.setProduct(product);
            demand.setUser(user);
            demand.setDemandQuantity(demandedQuantity);
            demand.setDemandDate(LocalDate.now());
            demand.setDemandStatus("PENDING"); // default new demand

            ProductDemand savedDemand = productDemandRepository.save(demand);

            // ✅ Also save in InventoryTransactions (your requirement)
            InventoryTransactions transaction = new InventoryTransactions();
            transaction.setProduct(product);
            transaction.setTransactionType("IN");  // better than "IN"
            transaction.setQuantity(demandedQuantity);
            transaction.setTransactionDate(LocalDate.now());
            transaction.setReference("Product #" + productId);
            transaction.setUserId(user);

            inventoryTransactionsRepository.save(transaction);

            return savedDemand;
        });
    }
    
    public List<ProductDemand> getAllDemands() {
        return productDemandRepository.findAll();
    }
    
    @Transactional
    public ProductDemand deliverDemandedProduct(Long demandId) {
        // Find the demand
        ProductDemand demand = productDemandRepository.findById(demandId)
                .orElseThrow(() -> new RuntimeException("Demand not found"));

        // Only deliver if it's not already delivered
        if (!"DELIVERED".equals(demand.getDemandStatus())) {
            // Update the demand status
            demand.setDemandStatus("DELIVERED");
            productDemandRepository.save(demand);

            // Update corresponding schedule status
            ProductionSchedule schedule = demand.getSchedule();
            if (schedule != null) {
                schedule.setPsStatus("DELIVERED");
                scheduleRepo.save(schedule);
            }

            // Optionally update the original product quantity if needed
             Product product = demand.getProduct();
             if (product != null && demand.getDemandQuantity() != null) {
                 double updatedQty = Optional.ofNullable(product.getProductQuantity()).orElse(0.0) 
                                     + demand.getDemandQuantity();
                 product.setProductQuantity(updatedQty);
//                 productDemandRepository.save(product);
             }
        }

        return demand;
    }
    
    public long countActiveDemands() {
        return productDemandRepository.countByDemandStatusNot("DELIVERED");
    }


}
