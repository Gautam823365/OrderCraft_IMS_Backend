package com.boot.ordercraft.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.boot.ordercraft.model.Product;

import java.util.List;

@Service
public class ProductEmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendLowStockMail(String toEmail, List<Product> lowStockProducts) throws MessagingException {
        if (lowStockProducts.isEmpty()) {
            return; // No need to send mail
        }

        StringBuilder body = new StringBuilder("⚠️ Low Stock Alert!\n\nThe following products are below threshold:\n\n");
        for (Product product : lowStockProducts) {
            body.append("👉 ")
                .append(product.getProductName())
                .append(" | Qty: ").append(product.getProductQuantity())
                .append(" | Threshold: ").append(product.getThreshold())
                .append("\n");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Low Stock Alert - Inventory Manager");
        message.setText(body.toString());

        mailSender.send(message);
    }
}





