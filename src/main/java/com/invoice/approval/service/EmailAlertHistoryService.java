package com.invoice.approval.service;

import com.invoice.approval.dto.EmailAlertStatsDTO;
import com.invoice.approval.dto.SubledgerAlertDTO;
import com.invoice.approval.entity.EmailAlertHistory;
import com.invoice.approval.repo.EmailAlertHistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmailAlertHistoryService {
    
    @Autowired
    private EmailAlertHistoryRepo emailAlertHistoryRepo;
    
    // Existing methods...
    public List<EmailAlertStatsDTO> getDailySummary(LocalDateTime startDate) {
        List<Object[]> results = emailAlertHistoryRepo.getDailySummary(startDate);
        return results.stream()
                .map(EmailAlertStatsDTO::new)
                .collect(Collectors.toList());
    }
    
    public List<EmailAlertStatsDTO> getLastNDaysStatistics(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return getDailySummary(startDate);
    }
    
    public EmailAlertHistory saveEmailAlert(EmailAlertHistory emailAlert) {
        return emailAlertHistoryRepo.save(emailAlert);
    }
    
    public List<EmailAlertHistory> getAllEmailAlerts() {
        return emailAlertHistoryRepo.findAll();
    }
    
    // NEW METHOD 1: Save consolidated alert history
 // Update method signature to accept List<String>
    public void saveConsolidatedAlertHistory(String toEmail, List<String> ccEmails, String salespersonName,
            List<SubledgerAlertDTO> subledgers, boolean success, 
            String errorMessage) {
        try {
            // Create a summary for the consolidated email
            String vendorSummary = subledgers.stream()
                    .limit(5)
                    .map(sub -> String.format("%s (%.1f%%)", 
                        sub.getSubledgerCode(), 
                        sub.getPercentage().doubleValue()))
                    .collect(Collectors.joining(", "));
            
            if (subledgers.size() > 5) {
                vendorSummary += String.format(" and %d more...", subledgers.size() - 5);
            }
            
            for (SubledgerAlertDTO subledger : subledgers) {
                EmailAlertHistory history = new EmailAlertHistory();
                
                // Use subledgerCode as vendorId
                Long vendorId = null;
                try {
                    if (subledger.getSubledgerCode() != null) {
                        vendorId = Long.parseLong(subledger.getSubledgerCode().replaceAll("[^0-9]", ""));
                    }
                } catch (NumberFormatException e) {
                    vendorId = null;
                }
                
                history.setVendorId(vendorId);
                history.setVendorName(subledger.getSubledgerName());
                history.setMailTo(toEmail);
                history.setAlertDate(LocalDateTime.now());
                history.setStatus(success ? "SUCCESS" : "FAILED");
                history.setCreatedBy("SUB_LEDGER_ALERT_SERVICE");
                history.setModifiedBy(salespersonName);
                
                // Combine error message with CC info
                StringBuilder messageBuilder = new StringBuilder();
                if (success) {
                    messageBuilder.append("Consolidated alert for ").append(subledgers.size())
                        .append(" customers: ").append(vendorSummary);
                } else if (errorMessage != null) {
                    messageBuilder.append(errorMessage);
                }
                
                // Add CC info if available
                if (ccEmails != null && !ccEmails.isEmpty()) {
                    messageBuilder.append(" | CC to: ").append(String.join(", ", ccEmails));
                }
                
                history.setErrorMessage(messageBuilder.toString());
                
                emailAlertHistoryRepo.save(history);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to save consolidated alert history: " + e.getMessage(), e);
        }
    }    
    // NEW METHOD 2: Save summary email history
    public void saveSummaryEmailHistory(String toEmail, List<SubledgerAlertDTO> subledgers, 
                                       boolean success, String errorMessage) {
        try {
            EmailAlertHistory history = new EmailAlertHistory();
            history.setVendorName("SYSTEM_SUMMARY");
            history.setMailTo(toEmail);
            history.setAlertDate(LocalDateTime.now());
            history.setStatus(success ? "SUCCESS" : "FAILED");
            history.setErrorMessage(errorMessage != null ? errorMessage : 
                String.format("Summary email for %d customers", subledgers.size()));
            history.setCreatedBy("SUB_LEDGER_ALERT_SERVICE");
            
            emailAlertHistoryRepo.save(history);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to save summary email history: " + e.getMessage(), e);
        }
    }
    
    // NEW METHOD 3: Save individual alert history (if needed)
   
    // Existing methods...
    public List<EmailAlertHistory> getByStatus(String status) {
        return emailAlertHistoryRepo.findByStatus(status);
    }
    
    public List<EmailAlertHistory> getByVendorEmail(String vendorEmail) {
        return emailAlertHistoryRepo.findByMailTo(vendorEmail);
    }
    
    public List<EmailAlertHistory> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return emailAlertHistoryRepo.findByAlertDateBetween(start, end);
    }
    
    public void saveIndividualAlertHistory(SubledgerAlertDTO subledger, String toEmail, 
            boolean success, String errorMessage) {
try {
EmailAlertHistory history = new EmailAlertHistory();

// Use subledgerCode as vendorId if no vendorId field exists
Long vendorId = null;
try {
if (subledger.getSubledgerCode() != null) {
vendorId = Long.parseLong(subledger.getSubledgerCode().replaceAll("[^0-9]", ""));
}
} catch (NumberFormatException e) {
vendorId = null;
}

history.setVendorId(vendorId);
history.setVendorName(subledger.getSubledgerName());
history.setMailTo(toEmail);
history.setAlertDate(LocalDateTime.now());
history.setStatus(success ? "SUCCESS" : "FAILED");
history.setErrorMessage(errorMessage);
history.setCreatedBy("SUB_LEDGER_ALERT_SERVICE");

emailAlertHistoryRepo.save(history);

} catch (Exception e) {
throw new RuntimeException("Failed to save individual alert history: " + e.getMessage(), e);
}
}
    
    public EmailAlertHistory createEmailAlert(Long invoiceId, Long vendorId, String vendorName,
                                             String vendorEmail, String status, String createdBy) {
        EmailAlertHistory alert = new EmailAlertHistory(
            invoiceId, vendorId, vendorName, vendorEmail,
            LocalDateTime.now(), status, createdBy
        );
        return saveEmailAlert(alert);
    }
    
    public EmailAlertHistory updateStatus(Long id, String status, String errorMessage) {
        EmailAlertHistory alert = emailAlertHistoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Email alert not found: " + id));
        
        alert.setStatus(status);
        alert.setErrorMessage(errorMessage);
        alert.setModifiedBy("SYSTEM");
        
        return saveEmailAlert(alert);
    }
    
 
    
}