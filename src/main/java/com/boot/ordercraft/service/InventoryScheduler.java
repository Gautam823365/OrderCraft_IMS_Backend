package com.boot.ordercraft.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.repository.ProductRepository;

import java.util.List;

@Component
public class InventoryScheduler {

    private final ProductRepository productRepository;
    private final ProductEmailService emailService;

    public InventoryScheduler(ProductRepository productRepository, ProductEmailService emailService) {
        this.productRepository = productRepository;
        this.emailService = emailService;
    }

//     Runs every day at 10:00 AM
    @Scheduled(cron = "0 0 10 * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    public void checkLowStockAndNotify() {
        // fetch products with low stock
        List<Product> lowStockProducts = productRepository.findByProductQuantityLessThanThreshold();

        // TODO: fetch logged-in manager email (for now hardcode)
        String managerEmail = "manager@inventory.com";

        try {
            emailService.sendLowStockMail(managerEmail, lowStockProducts);
            System.out.println("✅ Low stock email sent successfully.");
        } catch (Exception e) {
            System.err.println("❌ Error in sending email: " + e.getMessage());
        }
    }
}


