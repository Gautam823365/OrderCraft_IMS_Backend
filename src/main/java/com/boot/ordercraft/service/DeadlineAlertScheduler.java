package com.boot.ordercraft.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.boot.ordercraft.model.ProductionSchedule;
import com.boot.ordercraft.repository.ProductionScheduleRepository;

import jakarta.mail.MessagingException;

@Service
public class DeadlineAlertScheduler {

	@Autowired
    private  ProductionScheduleRepository scheduleRepo;
	
	@Autowired
    private  DeadlineEmailService emailService;


    // Run every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
//	@Scheduled(cron = "0 * * * * ?")
    public void checkDeadlines() throws MessagingException {
        LocalDate today = LocalDate.now();

        // Get tasks whose deadline is today or tomorrow
        List<ProductionSchedule> upcoming = scheduleRepo.findByPsDeadline(today.plusDays(1));

        if (!upcoming.isEmpty()) {
            // ✅ Replace with real recipient from your User table if available
            emailService.sendDeadlineAlert("paras@bosch.in", upcoming);
        }
    }
}

