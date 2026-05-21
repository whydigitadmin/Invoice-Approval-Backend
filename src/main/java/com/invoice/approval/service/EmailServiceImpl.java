package com.invoice.approval.service;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.invoice.approval.entity.EmployeeAttachmentVO;
import com.invoice.approval.repo.EmployeeAttachmentRepo;

@Component
public class EmailServiceImpl {
	 @Autowired
	    private EmailService emailService;
	    
	    @Autowired
	    private EmployeeAttachmentRepo employeeAttachmentRepo;

	    // Send promotion emails every day at 12:55 PM IST
//	    @Scheduled(cron = "0 * * * * ?")
	    public void sendDailyPromotionEmails() {
	        System.out.println("Starting scheduled promotion email job...");
	        
	        // Get all employees with attachments that are marked for promotion
	        List<EmployeeAttachmentVO> promotions = employeeAttachmentRepo.findActiveRecords("T");
	        
	        for (EmployeeAttachmentVO promotion : promotions) {
	            try {
	                // Schedule email for immediate delivery
	                promotion.setScheduledTime(LocalDateTime.now());
	                promotion.setScheduled(true);
	                promotion.setEmailSubject("🎉 Congratulations on Your Promotion!");
	                employeeAttachmentRepo.save(promotion);
	                
	                System.out.println("Scheduled promotion email for: " + promotion.getEmployeeEmail());
	            } catch (Exception e) {
	                System.err.println("Failed to schedule promotion email for: " + promotion.getEmployeeEmail());
	                e.printStackTrace();
	            }
	        }
	        
	        System.out.println("Promotion email scheduling job completed.");
	    }
	    
	    
	

	    // Send daily reports every day at 11:58 AM IST
//	    @Scheduled(cron = "0 32 14 * * *", zone = "Asia/Kolkata")
	    public void sendDailyReport() {
	        System.out.println("Starting daily report email job...");
	        
	        // Prepare recipients
	        List<String> recipients = List.of(
	            "gjayabalan08@gmail.com",
	            "jayabalan.guru@uniworld-logistics.com"
	        );

	        // Create a new email record for each recipient
	        for (String recipient : recipients) {
	            try {
	                EmployeeAttachmentVO report = new EmployeeAttachmentVO();
	                report.setEmployeeEmail(recipient);
	                report.setScheduledTime(LocalDateTime.now());
	                report.setScheduled(true);
	                report.setEmailSubject("Daily Report");
	                report.setActive("F"); // Not a promotion email
	                
	                // You might want to add report-specific attachments here
	                // report.setTextFileData(...);
	                // report.setPdfFileData(...);
	                
	                employeeAttachmentRepo.save(report);
	                
	                System.out.println("Scheduled daily report for: " + recipient);
	            } catch (Exception e) {
	                System.err.println("Failed to schedule daily report for: " + recipient);
	                e.printStackTrace();
	            }
	        }
	        
	        System.out.println("Daily report scheduling job completed.");
	    }
	}