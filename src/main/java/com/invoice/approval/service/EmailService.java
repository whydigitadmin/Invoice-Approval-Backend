package com.invoice.approval.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.SubledgerAlertDTO;
import com.invoice.approval.entity.EmailAlertHistory;
import com.invoice.approval.entity.EmployeeAttachmentVO;
import com.invoice.approval.repo.EmailAlertHistoryRepo;
import com.invoice.approval.repo.EmployeeAttachmentRepo;

@Service
public class EmailService {
    
    @Autowired
    private EmployeeAttachmentRepo empattachRepo;

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailAlertHistoryRepo emailAlertHistoryRepo;
    
    @Value("#{'${email.bcc}'.split(',')}")
    private List<String> bccEmails;

//    @Scheduled(fixedRate = 60000) // Runs every minute
    public void processScheduledEmails() {
        List<EmployeeAttachmentVO> scheduledEmails = empattachRepo
            .findPendingScheduledEmails(LocalDateTime.now());
        
        for (EmployeeAttachmentVO email : scheduledEmails) {
            try {
                if (validatePdfData(email.getPdfFileData())) {
                    sendEmailWithPdf(email);
                } else {
                    System.err.println("Invalid PDF data for email: " + email.getEmployeeEmail());
                }
            } catch (Exception e) {
                System.err.println("Failed to send email to: " + email.getEmployeeEmail());
                e.printStackTrace();
            }
        }
    }
    
    // ==================== NAME EXTRACTION METHODS ====================
    
    /**
     * Extract name from email address
     * Takes part before @ and before first dot
     * Examples:
     * - jayabalan.guru@uniworld-logistics.com -> Jayabalan
     * - john.doe@company.com -> John
     * - jane_doe@company.com -> Jane
     * - alex@company.com -> Alex
     */
    private String extractNameFromEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        // Get everything before @
        String namePart = email.split("@")[0];
        
        // Get only the part before the first dot
        if (namePart.contains(".")) {
            namePart = namePart.split("\\.")[0];
        }
        
        // Replace underscores with spaces
        namePart = namePart.replace("_", " ");
        
        // Capitalize first letter only
        if (!namePart.isEmpty()) {
            namePart = Character.toUpperCase(namePart.charAt(0)) + 
                      (namePart.length() > 1 ? namePart.substring(1).toLowerCase() : "");
        }
        
        return namePart.isEmpty() ? null : namePart;
    }
    
    /**
     * Format CC emails with names for display in email body
     * Returns: "John (john@company.com), Jane (jane@company.com)"
     */
    private String formatCCEmailsWithNames(List<String> ccEmails) {
        if (ccEmails == null || ccEmails.isEmpty()) {
            return null;
        }
        
        List<String> formattedCC = new ArrayList<>();
        for (String ccEmail : ccEmails) {
            String name = extractNameFromEmail(ccEmail);
            if (name != null) {
                formattedCC.add(name + " (" + ccEmail + ")");
            } else {
                formattedCC.add(ccEmail);
            }
        }
        
        return String.join(", ", formattedCC);
    }
    
    // ==================== EMAIL SENDING METHODS ====================
    
    /**
     * Send individual subledger alert email
     */
    public void sendSubledgerAlertEmail(SubledgerAlertDTO subledger) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        String recipientEmail = subledger.getMailid();
        String recipientName = extractNameFromEmail(recipientEmail);
        
        // Set email properties
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(recipientEmail);
        helper.setSubject("🚨 Credit Limit Alert: " + subledger.getSubledgerCode() + " - " + subledger.getSubledgerName());
        
        if (bccEmails != null && !bccEmails.isEmpty()) {
            helper.setBcc(bccEmails.toArray(new String[0]));
        }
        
        // Build email body with personalized greeting
        String body = buildSubledgerAlertEmailBody(subledger, recipientName);
        helper.setText(body, true);
        
        try {
            // Send email
            mailSender.send(message);
            System.out.println("Subledger alert email sent to: " + recipientEmail);
            
            // SAVE SUCCESS HISTORY
            EmailAlertHistory history = new EmailAlertHistory();
            history.setMailTo(recipientEmail);
            history.setAlertDate(LocalDateTime.now());
            history.setStatus("SUCCESS");
            history.setCreatedBy("SYSTEM");
            history.setCreatedDate(LocalDateTime.now());
            history.setVendorId(subledger.getVendorId() != null ? subledger.getVendorId().longValue() : null);
            history.setVendorName(subledger.getSubledgerName());
            history.setSalesPersonName(recipientName);
            
            emailAlertHistoryRepo.save(history);
            
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + recipientEmail);
            
            // SAVE FAILURE HISTORY
            EmailAlertHistory history = new EmailAlertHistory();
            history.setMailTo(recipientEmail);
            history.setAlertDate(LocalDateTime.now());
            history.setStatus("FAILED");
            history.setErrorMessage(e.getMessage());
            history.setCreatedBy("SYSTEM");
            history.setCreatedDate(LocalDateTime.now());
            history.setVendorId(subledger.getVendorId() != null ? subledger.getVendorId().longValue() : null);
            history.setVendorName(subledger.getSubledgerName());
            
            emailAlertHistoryRepo.save(history);
            
            throw e;
        }
    }
    
    /**
     * Send summary alert email with CC support
     * This method properly handles names for both TO and CC recipients
     */
    /**
     * Send summary alert email with CC support
     * This method sends separate personalized emails to each CC recipient
     */
    public void sendSummaryAlertEmail(String toEmail, List<String> ccEmails, String subject, String emailContent) throws MessagingException {
        
        // First, send email to the main recipient (TO)
        sendPersonalizedEmail(toEmail, subject, emailContent, null);
        
        // Then, send separate personalized emails to each CC recipient
        if (ccEmails != null && !ccEmails.isEmpty()) {
            for (String ccEmail : ccEmails) {
                try {
                    sendPersonalizedEmail(ccEmail, subject, emailContent, null);
                    System.out.println("CC email sent to: " + ccEmail);
                } catch (Exception e) {
                    System.err.println("Failed to send CC email to: " + ccEmail + " - " + e.getMessage());
                }
            }
        }
    }

    /**
     * Send personalized email to a single recipient
     */
    private void sendPersonalizedEmail(String recipientEmail, String subject, String emailContent, List<String> additionalCC) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        // Extract name from recipient email
        String recipientName = extractNameFromEmail(recipientEmail);
        String personalizedName = (recipientName != null) ? recipientName : "User";
        
        // Set email properties
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(recipientEmail);
        
        // Add additional CC if provided (for the main TO email)
        if (additionalCC != null && !additionalCC.isEmpty()) {
            helper.setCc(additionalCC.toArray(new String[0]));
        }
        
        if (bccEmails != null && !bccEmails.isEmpty()) {
            helper.setBcc(bccEmails.toArray(new String[0]));
        }
        
        helper.setSubject(subject);
        
        // Replace placeholders in email content
        String personalizedContent = emailContent.replace("{TO_NAME}", personalizedName);
        
        // If there's a CC placeholder, remove it for individual emails
        personalizedContent = personalizedContent.replace("{CC_NAMES}", "");
        
        helper.setText(personalizedContent, true);
        
        try {
            // Send email
            mailSender.send(message);
            System.out.println("Personalized email sent to: " + recipientEmail);
            
            // SAVE HISTORY
            EmailAlertHistory history = new EmailAlertHistory();
            history.setMailTo(recipientEmail);
            history.setCcEmails(additionalCC != null ? String.join(",", additionalCC) : null);
            history.setAlertDate(LocalDateTime.now());
            history.setStatus("SUCCESS");
            history.setCreatedBy("SYSTEM");
            history.setCreatedDate(LocalDateTime.now());
            history.setSalesPersonName(personalizedName);
            
            emailAlertHistoryRepo.save(history);
            
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + recipientEmail);
            
            // SAVE FAILURE HISTORY
            EmailAlertHistory history = new EmailAlertHistory();
            history.setMailTo(recipientEmail);
            history.setCcEmails(additionalCC != null ? String.join(",", additionalCC) : null);
            history.setAlertDate(LocalDateTime.now());
            history.setStatus("FAILED");
            history.setErrorMessage(e.getMessage());
            history.setCreatedBy("SYSTEM");
            history.setCreatedDate(LocalDateTime.now());
            
            emailAlertHistoryRepo.save(history);
            
            throw e;
        }
    }

    /**
     * Send summary alert email with CC support (original method - modified)
     */
    public void sendSummaryAlertEmailWithCCList(String toEmail, List<String> ccEmails, String subject, String emailContent) throws MessagingException {
        // Format CC emails with names for display in the main email body
        String formattedCCEmails = formatCCEmailsWithNames(ccEmails);
        
        // Replace CC placeholder in email content for the main email
        String mainEmailContent = emailContent;
        if (formattedCCEmails != null) {
            mainEmailContent = emailContent.replace("{CC_NAMES}", formattedCCEmails);
        }
        
        // Send main email to TO recipient
        sendPersonalizedEmail(toEmail, subject, mainEmailContent, ccEmails);
        
        // Send separate personalized emails to each CC recipient
        if (ccEmails != null && !ccEmails.isEmpty()) {
            for (String ccEmail : ccEmails) {
                try {
                    // For CC emails, remove CC placeholder and don't add CC list
                    String ccEmailContent = emailContent.replace("{CC_NAMES}", "");
                    sendPersonalizedEmail(ccEmail, subject, ccEmailContent, null);
                    System.out.println("CC email sent to: " + ccEmail + " with name: " + extractNameFromEmail(ccEmail));
                } catch (Exception e) {
                    System.err.println("Failed to send CC email to: " + ccEmail + " - " + e.getMessage());
                }
            }
        }
    }


    /**
     * Overloaded method for backward compatibility (without CC)
     */
    public void sendSummaryAlertEmail(String toEmail, String subject, String emailContent) throws MessagingException {
        sendSummaryAlertEmail(toEmail, null, subject, emailContent);
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    private String buildSubledgerAlertEmailBody(SubledgerAlertDTO subledger, String recipientName) {
        String priority = subledger.getPercentage().intValue() >= 90 ? "HIGH" : "MEDIUM";
        String priorityColor = subledger.getPercentage().intValue() >= 90 ? "#FF0000" : "#FFA500";
        
        String greeting = (recipientName != null) ? "Dear " + recipientName + "," : "Dear Customer,";
        
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<style>" +
               "body { font-family: Arial, sans-serif; }" +
               ".header { background: #2c3e50; color: white; padding: 20px; text-align: center; }" +
               ".content { padding: 20px; }" +
               ".alert-box { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 15px 0; }" +
               ".details-table { width: 100%; border-collapse: collapse; margin: 15px 0; }" +
               ".details-table td { padding: 8px; border-bottom: 1px solid #ddd; }" +
               ".details-table tr:last-child td { border-bottom: none; }" +
               ".priority { color: " + priorityColor + "; font-weight: bold; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='header'>" +
               "<h1>🚨 Credit Limit Alert</h1>" +
               "</div>" +
               "<div class='content'>" +
               "<p>" + greeting + "</p>" +
               "<div class='alert-box'>" +
               "<h2 class='priority'>" + priority + " PRIORITY ALERT</h2>" +
               "<p>Credit utilization has reached " + subledger.getPercentage() + "% for customer: <strong>" + 
               subledger.getSubledgerName() + "</strong></p>" +
               "</div>" +
               "<table class='details-table'>" +
               "<tr><td><strong>Customer Code:</strong></td><td>" + getSafeString(subledger.getSubledgerCode()) + "</td></tr>" +
               "<tr><td><strong>Customer Name:</strong></td><td>" + getSafeString(subledger.getSubledgerName()) + "</td></tr>" +
               "<tr><td><strong>Category:</strong></td><td>" + getSafeString(subledger.getCategory()) + "</td></tr>" +
               "<tr><td><strong>Control Office:</strong></td><td>" + getSafeString(subledger.getCtrlOffice()) + "</td></tr>" +
               "<tr><td><strong>Salesperson:</strong></td><td>" + getSafeString(subledger.getSalesperson()) + "</td></tr>" +
               "<tr><td><strong>Credit Limit:</strong></td><td>₹" + getSafeBigDecimal(subledger.getCreditLimit()) + "</td></tr>" +
               "<tr><td><strong>Credit Days:</strong></td><td>" + getSafeInteger(subledger.getCreditDays()) + " days</td></tr>" +
               "<tr><td><strong>Total Due:</strong></td><td>₹" + getSafeBigDecimal(subledger.getTotdue()) + "</td></tr>" +
               "<tr><td><strong>Utilization %:</strong></td><td><strong>" + subledger.getPercentage() + "%</strong></td></tr>" +
               "</table>" +
               "<p><em>Please review this account and take appropriate action.</em></p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    private String getSafeString(String value) {
        return value != null && !value.trim().isEmpty() ? value : "N/A";
    }
    
    private String getSafeBigDecimal(BigDecimal value) {
        return value != null ? value.toString() : "0";
    }
    
    private String getSafeInteger(Integer value) {
        return value != null ? value.toString() : "0";
    }
    
    private void sendEmailWithPdf(EmployeeAttachmentVO email) 
            throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        String recipientEmail = email.getEmployeeEmail();
        String recipientName = extractNameFromEmail(recipientEmail);
        
        // Set basic email properties
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(recipientEmail);
        helper.setSubject(email.getEmailSubject());
        
        if (bccEmails != null && !bccEmails.isEmpty()) {
            helper.setBcc(bccEmails.toArray(new String[0]));
        }
        
        // Build email body with preview instructions
        String body = buildEmailBody(email, recipientName);
        helper.setText(body, true);
        
        // Attach PDF
        attachPdf(helper, email);
        
        // Additional headers for better client compatibility
        message.setHeader("X-Attachment-Id", "pdf_" + System.currentTimeMillis());
        message.saveChanges();
        
        try {
            // Send email
            mailSender.send(message);
            
            // Update status
            email.setSent(true);
            empattachRepo.save(email);
            System.out.println("Email successfully sent to: " + recipientEmail);
            
            // SAVE HISTORY FOR PDF EMAIL
            EmailAlertHistory history = new EmailAlertHistory();
            history.setMailTo(recipientEmail);
            history.setAlertDate(LocalDateTime.now());
            history.setStatus("SUCCESS");
            history.setCreatedBy("SYSTEM");
            history.setCreatedDate(LocalDateTime.now());
            history.setVendorName(recipientName);
            
            emailAlertHistoryRepo.save(history);
            
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + recipientEmail);
            
            // SAVE FAILURE HISTORY
            EmailAlertHistory history = new EmailAlertHistory();
            history.setMailTo(recipientEmail);
            history.setAlertDate(LocalDateTime.now());
            history.setStatus("FAILED");
            history.setErrorMessage(e.getMessage());
            history.setCreatedBy("SYSTEM");
            history.setCreatedDate(LocalDateTime.now());
            
            emailAlertHistoryRepo.save(history);
            
            throw e;
        }
    }
    
    private void attachPdf(MimeMessageHelper helper, EmployeeAttachmentVO email) 
            throws MessagingException, IOException {
        if (email.getPdfFileData() == null || email.getPdfFileData().length == 0) {
            return;
        }
        
        // Set filename
        String filename = email.getPdfFileName();
        if (filename == null || filename.trim().isEmpty()) {
            filename = "document_" + System.currentTimeMillis() + ".pdf";
        } else if (!filename.toLowerCase().endsWith(".pdf")) {
            filename += ".pdf";
        }
        
        // Create the resource
        ByteArrayResource resource = new ByteArrayResource(email.getPdfFileData());
        
        // Add as attachment
        helper.addAttachment(filename, resource);
    }
    
    private String buildEmailBody(EmployeeAttachmentVO email, String recipientName) {
        String name = (recipientName != null) ? recipientName : extractNameFromEmail(email.getEmployeeEmail());
        if (name == null) name = "User";
        
        String content = getTextContent(email);
        
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<style>body { font-family: Arial, sans-serif; }</style>" +
               "</head>" +
               "<body>" +
               "<h2>Dear " + name + ",</h2>" +
               "<div>" + content + "</div>" +
               "<div style='margin: 20px 0; padding: 15px; background: #f5f5f5;'>" +
               "<h3>Your Document</h3>" +
               "<p>Please find your document attached to this email.</p>" +
               "<p style='color: #666;'>Some email clients may show a preview, " +
               "otherwise please download the attachment.</p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    private boolean validatePdfData(byte[] pdfData) {
        if (pdfData == null || pdfData.length < 5) return false;
        // Check PDF magic number (%PDF-)
        return pdfData[0] == 0x25 && pdfData[1] == 0x50 && 
               pdfData[2] == 0x44 && pdfData[3] == 0x46 && 
               pdfData[4] == 0x2D;
    }
    
    private String getTextContent(EmployeeAttachmentVO mapping) {
        if (mapping.getTextFileData() == null || mapping.getTextFileData().length == 0) {
            return "<p>No additional notes provided.</p>";
        }
        return new String(mapping.getTextFileData(), StandardCharsets.UTF_8);
    }
}