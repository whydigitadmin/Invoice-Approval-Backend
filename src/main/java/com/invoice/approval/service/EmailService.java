package com.invoice.approval.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.SubledgerAlertDTO;
import com.invoice.approval.entity.EmployeeAttachmentVO;
import com.invoice.approval.repo.EmployeeAttachmentRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.ByteArrayResource;

@Service
public class EmailService {
    
    @Autowired
    private EmployeeAttachmentRepo empattachRepo;

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("#{'${email.bcc}'.split(',')}")
    private List<String> bccEmails;

    @Scheduled(fixedRate = 60000) // Runs every minute
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
    
 // Add this method for individual subledger alerts
    public void sendSubledgerAlertEmail(SubledgerAlertDTO subledger) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        // Set email properties
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(subledger.getMailid());
        helper.setSubject("🚨 Credit Limit Alert: " + subledger.getSubledgerCode() + " - " + subledger.getSubledgerName());
        
        if (bccEmails != null && !bccEmails.isEmpty()) {
            helper.setBcc(bccEmails.toArray(new String[0]));
        }
        
        // Build email body
        String body = buildSubledgerAlertEmailBody(subledger);
        helper.setText(body, true);
        
        // Send email
        mailSender.send(message);
//        LOGGER.info("Subledger alert email sent to: {}", subledger.getMailid());
    }

    // Add this method for summary alerts
    public void sendSummaryAlertEmail(String toEmail, String subject, String emailContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        // Set email properties
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        
        if (bccEmails != null && !bccEmails.isEmpty()) {
            helper.setBcc(bccEmails.toArray(new String[0]));
        }
        
        helper.setText(emailContent, true);
        
        // Send email
        mailSender.send(message);
//        LOGGER.info("Summary alert email sent to: {}", toEmail);
    }
    
    // Helper methods for EmailService
    private String getSafeString(String value) {
        return value != null ? value : "N/A";
    }

    private String getSafeBigDecimal(BigDecimal value) {
        return value != null ? value.toString() : "0";
    }

    private String getSafeInteger(Integer value) {
        return value != null ? value.toString() : "0";
    }

    private String buildSubledgerAlertEmailBody(SubledgerAlertDTO subledger) {
        String priority = subledger.getPercentage().intValue() >= 90 ? "HIGH" : "MEDIUM";
        String priorityColor = subledger.getPercentage().intValue() >= 90 ? "#FF0000" : "#FFA500";
        
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
               "<div class='alert-box'>" +
               "<h2 class='priority'>" + priority + " PRIORITY ALERT</h2>" +
               "<p>Credit utilization has reached " + subledger.getPercentage() + "% for customer: <strong>" + 
               subledger.getSubledgerName() + "</strong></p>" +
               "</div>" +
               "<table class='details-table'>" +
               "<tr><td><strong>Customer Code:</strong></td><td>" + getSafeString(subledger.getSubledgerCode()) + "</td></tr>" +
               "<tr><td><strong>Customer Name:</strong></td><td>" + getSafeString(subledger.getSubledgerName()) + "</td></tr>" +
               "<tr><td><strong>Control Office:</strong></td><td>" + getSafeString(subledger.getCategory()) + "</td></tr>" +
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

    private void sendEmailWithPdf(EmployeeAttachmentVO email) 
            throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        // Set basic email properties
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(email.getEmployeeEmail());
        helper.setSubject(email.getEmailSubject());
        
        if (bccEmails != null && !bccEmails.isEmpty()) {
            helper.setBcc(bccEmails.toArray(new String[0]));
        }
        
        // Build email body with preview instructions
        String body = buildEmailBody(email);
        helper.setText(body, true);
        
        // Attach PDF
        attachPdf(helper, email);
        
        // Additional headers for better client compatibility
        message.setHeader("X-Attachment-Id", "pdf_" + System.currentTimeMillis());
        message.saveChanges();
        
        // Send email
        mailSender.send(message);
        
        // Update status
        email.setSent(true);
        empattachRepo.save(email);
        System.out.println("Email successfully sent to: " + email.getEmployeeEmail());
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
        
        // If you want to also add as inline (optional)
        // helper.addInline("pdfContent", resource, "application/pdf");
    }
    
    
    private String buildEmailBody(EmployeeAttachmentVO email) {
        String name = extractNameFromEmail(email.getEmployeeEmail());
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
    
    private String extractNameFromEmail(String email) {
        String namePart = email.split("@")[0];
        namePart = namePart.replaceAll("[^a-zA-Z0-9]", " ");
        namePart = Arrays.stream(namePart.split(" "))
                       .map(word -> word.isEmpty() ? "" : 
                           Character.toUpperCase(word.charAt(0)) + word.substring(1))
                       .reduce((a, b) -> a + " " + b).orElse("User");
        return namePart;
    }


//    public String getStyledBody() {
//        return "<!DOCTYPE html>" +
//               "<html>" +
//               "<head>" +
//               "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
//               "<style>" +
//               "  @import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600;700&display=swap');" +
//               "</style>" +
//               "</head>" +
//               "<body style=\"margin:0; padding:0; font-family: 'Montserrat', Arial, sans-serif;\">" +
//               
//               // Main container with background image
//               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" bgcolor=\"#f5f7fa\" style=\"background-image: url('https://example.com/path/to/celebration-bg.jpg'); background-size: cover;\">" +
//               "<tr>" +
//               "<td align=\"center\" style=\"padding:40px 0;\">" +
//               
//               // Email container
//               "<table width=\"600\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" bgcolor=\"#ffffff\" style=\"border-radius:8px; overflow:hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1);\">" +
//               
//               // Header with celebration image
//               "<tr>" +
//               "<td bgcolor=\"#4CAF50\" style=\"padding:40px; text-align:center; background: linear-gradient(135deg, #4CAF50 0%, #2E7D32 100%);\">" +
//               "<img src=\"https://example.com/path/to/celebration-icon.png\" alt=\"Celebration\" width=\"80\" style=\"display:block; margin:0 auto 15px;\">" +
//               "<h1 style=\"color:white; margin:0; font-size:28px; font-weight:700;\">🎉 Congratulations on Your Promotion!</h1>" +
//               "<p style=\"color:rgba(255,255,255,0.9); margin:10px 0 0; font-size:16px; line-height:1.5;\">We recognize and appreciate your valuable contributions</p>" +
//               "</td>" +
//               "</tr>" +
//               
//               // Main content
//               "<tr>" +
//               "<td style=\"padding:40px;\">" +
//               "<h2 style=\"color:#2d3748; margin-top:0; font-size:22px; font-weight:600;\">Dear {name},</h2>" +
//               
//               // Personal content section
//               "<div style=\"background:#f8fafc; padding:25px; border-radius:8px; margin-bottom:25px; border-left:4px solid #4CAF50;\">" +
//               "{personal_content}" +
//               "</div>" +
//               
//               // Key details
//               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"margin-bottom:25px;\">" +
//               "<tr>" +
//               "<td width=\"50%\" valign=\"top\" style=\"padding-right:15px;\">" +
//               "<div style=\"background:#e8f5e9; padding:15px; border-radius:6px;\">" +
//               "<h3 style=\"color:#2E7D32; margin-top:0; font-size:16px;\">Effective Date</h3>" +
//               "<p style=\"color:#4a5568; margin:5px 0 0;\">{effective_date}</p>" +
//               "</div>" +
//               "</td>" +
//               "<td width=\"50%\" valign=\"top\" style=\"padding-left:15px;\">" +
//               "<div style=\"background:#e8f5e9; padding:15px; border-radius:6px;\">" +
//               "<h3 style=\"color:#2E7D32; margin-top:0; font-size:16px;\">New Position</h3>" +
//               "<p style=\"color:#4a5568; margin:5px 0 0;\">{new_position}</p>" +
//               "</div>" +
//               "</td>" +
//               "</tr>" +
//               "</table>" +
//               
//               // CTA Button
//               "<a href=\"{hr_portal_link}\" style=\"display:inline-block; background:#4CAF50; color:white; text-decoration:none; padding:12px 30px; border-radius:6px; font-weight:600; text-align:center; margin:15px 0 25px; font-size:16px;\">" +
//               "View Details in HR Portal" +
//               "</a>" +
//               
//               // Footer
//               "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
//               "<tr>" +
//               "<td style=\"border-top:1px solid #e2e8f0; padding:25px 0 0;\">" +
//               "<p style=\"font-size:14px; color:#718096; text-align:center; margin-bottom:5px;\">" +
//               "Please find attached your official promotion letter" +
//               "</p>" +
//               "<p style=\"font-size:12px; color:#a0aec0; text-align:center; margin:0;\">" +
//               "This is an automated notification. For questions, contact <a href=\"mailto:hr@company.com\" style=\"color:#4CAF50; text-decoration:none;\">HR Department</a>" +
//               "</p>" +
//               "</td>" +
//               "</tr>" +
//               "</table>" +
//               "</td>" +
//               "</tr>" +
//               "</table>" +
//               "</td>" +
//               "</tr>" +
//               "</table>" +
//               "</body>" +
//               "</html>";
//    }
}