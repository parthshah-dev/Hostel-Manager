package com.example.hostelmanagement.scheduler;

import com.example.hostelmanagement.entity.PaymentStatus;
import com.example.hostelmanagement.entity.Rent;
import com.example.hostelmanagement.entity.Tenant;
import com.example.hostelmanagement.repository.RentRepository;
import com.example.hostelmanagement.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduler component executing automated background tasks for Rent notifications.
 */
@Component
@Slf4j
public class RentReminderScheduler {

    private final RentRepository rentRepository;
    private final EmailService emailService;

    public RentReminderScheduler(RentRepository rentRepository, EmailService emailService) {
        this.rentRepository = rentRepository;
        this.emailService = emailService;
    }

    /**
     * Automated task running at 10:00 AM on the 1st of every month to email rent due reminders
     * to all active tenants who have PENDING rents.
     * Cron format: second, minute, hour, day of month, month, day of week
     */
    @Scheduled(cron = "${rent.reminder.cron:0 0 10 1 * *}")
    public void sendMonthlyRentDueReminders() {
        log.info("Starting automated monthly rent due reminders scheduler");
        try {
            List<Rent> pendingRents = rentRepository.findByPaymentStatus(PaymentStatus.PENDING);
            int sentCount = 0;
            for (Rent rent : pendingRents) {
                Tenant tenant = rent.getTenant();
                if (tenant != null && Boolean.TRUE.equals(tenant.getActive())) {
                    log.info("Sending rent due reminder to tenant: {} for month: {}", tenant.getFullName(), rent.getRentMonth());
                    emailService.sendRentDueReminderEmail(
                            tenant.getEmail(),
                            tenant.getFullName(),
                            rent.getRentMonth(),
                            rent.getDueAmount()
                    );
                    sentCount++;
                }
            }
            log.info("Finished automated monthly rent due reminders scheduler. Total reminders sent: {}", sentCount);
        } catch (Exception e) {
            log.error("Error occurred during automated rent due reminders processing", e);
        }
    }
}
