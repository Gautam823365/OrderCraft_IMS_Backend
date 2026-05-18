package com.boot.ordercraft.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boot.ordercraft.model.ProductionSchedule;
import com.boot.ordercraft.repository.ProductionScheduleRepository;

@Service
public class ProductionScheduleScheduler {

    private final ProductionScheduleRepository productionScheduleRepository;

    public ProductionScheduleScheduler(ProductionScheduleRepository productionScheduleRepository) {
        this.productionScheduleRepository = productionScheduleRepository;
    }

    // Run every day at midnight (adjust cron as needed)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateReadySchedules() {
        LocalDate today = LocalDate.now();
        List<ProductionSchedule> schedules = productionScheduleRepository.findByPsEndDateBeforeAndPsStatusNot(today, "READY");

        for (ProductionSchedule schedule : schedules) {
            schedule.setPsStatus("READY");
        }

        productionScheduleRepository.saveAll(schedules);
    }
}
