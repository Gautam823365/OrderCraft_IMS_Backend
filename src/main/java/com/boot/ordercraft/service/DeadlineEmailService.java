package com.boot.ordercraft.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.boot.ordercraft.model.ProductionSchedule;

import java.time.LocalDate;
import java.util.List;

@Service
public class DeadlineEmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendDeadlineAlert(String toEmail, List<ProductionSchedule> schedules) throws MessagingException {
        if (schedules.isEmpty()) {
            return; // Nothing to send
        }

        StringBuilder body = new StringBuilder("⏰ Production Deadline Alert!\n\nThe following schedules are near deadline:\n\n");
        for (ProductionSchedule ps : schedules) {
            body.append("👉 Product: ")
                .append(ps.getPsProductId().getProductName())
                .append(" | Deadline: ").append(ps.getPsDeadline())
                .append(" | Status: ").append(ps.getPsStatus())
                .append("\n");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Production Deadline Alert - OrderCraft");
        message.setText(body.toString());

        mailSender.send(message);
    }
}
