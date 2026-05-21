package com.invoice.approval.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.approval.entity.EmailSendingHistory;  // FIXED: Added proper import
import com.invoice.approval.entity.EmailStatus;          // FIXED: Added proper import
import com.invoice.approval.repo.EmailSendingHistoryRepo;

@Service
public class EmailHistoryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailHistoryService.class);
    
    @Autowired
    private EmailSendingHistoryRepo emailHistoryRepo;
    
    /**
     * Record successful email sending
     */
    public void recordSuccess(String salespersonEmail, String salespersonName, 
                               String recipientEmail, List<String> ccEmails,
                               String jobType, int customerCount, int invoiceCount,
                               BigDecimal totalOutstanding) {
        try {
            EmailSendingHistory history = new EmailSendingHistory();
            history.setSalespersonEmail(salespersonEmail);
            history.setSalespersonName(salespersonName);
            history.setRecipientEmail(recipientEmail);
            history.setCcEmails(ccEmails != null ? String.join(", ", ccEmails) : "");
            history.setBccEmail("");
            history.setEmailSubject("Outstanding Statement");
            history.setStatus(EmailStatus.SUCCESS);
            history.setJobType(jobType);
            history.setTotalOutstandingAmount(totalOutstanding);
            history.setCustomerCount(customerCount);
            history.setInvoiceCount(invoiceCount);
            history.setErrorMessage(null); // No error for success
            history.setSentAt(LocalDateTime.now());
            
            emailHistoryRepo.save(history);
            LOGGER.debug("Email success record saved for: {}", salespersonEmail);
        } catch (Exception e) {
            LOGGER.error("Failed to save email success record: {}", e.getMessage());
        }
    }
    
    /**
     * Record failed email sending
     */
    public void recordFailure(String salespersonEmail, String salespersonName,
                               String recipientEmail, List<String> ccEmails,
                               String jobType, String errorMessage) {
        try {
            EmailSendingHistory history = new EmailSendingHistory();
            history.setSalespersonEmail(salespersonEmail);
            history.setSalespersonName(salespersonName != null ? salespersonName : "Unknown");
            history.setRecipientEmail(recipientEmail);
            history.setCcEmails(ccEmails != null ? String.join(", ", ccEmails) : "");
            history.setBccEmail("");
            history.setEmailSubject("Outstanding Statement");
            history.setStatus(EmailStatus.FAILED);
            history.setJobType(jobType);
            history.setErrorMessage(errorMessage);
            history.setSentAt(LocalDateTime.now());
            
            emailHistoryRepo.save(history);
            LOGGER.debug("Email failure record saved for: {}", salespersonEmail);
        } catch (Exception e) {
            LOGGER.error("Failed to save email failure record: {}", e.getMessage());
        }
    }
    
    /**
     * Record email sending with custom parameters
     */
    public void recordEmail(EmailSendingHistory history) {
        try {
            if (history.getSentAt() == null) {
                history.setSentAt(LocalDateTime.now());
            }
            emailHistoryRepo.save(history);
            LOGGER.debug("Email record saved for: {}", history.getSalespersonEmail());
        } catch (Exception e) {
            LOGGER.error("Failed to save email record: {}", e.getMessage());
        }
    }
}