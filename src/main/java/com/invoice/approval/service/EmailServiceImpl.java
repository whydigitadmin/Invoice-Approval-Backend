package com.invoice.approval.service;



import com.invoice.approval.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EmailServiceImpl {

    @Autowired
    private EmailService emailService;

    // ✅ Runs every day at 4:00 PM IST
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Kolkata")
    public void sendDailyReport() {
        List<String> recipients = Arrays.asList(
            "gjayabalan08@gmail.com",
            "jayabalan.guru@uniworld-logistics.com"
        );

        String subject = "Daily Report";
        String body = emailService.getStyledBody();

        emailService.sendMail(recipients, subject, body);

        System.out.println("Scheduled email sent at 4 PM.");
    }
}
