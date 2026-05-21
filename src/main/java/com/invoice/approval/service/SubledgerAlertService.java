package com.invoice.approval.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.SubledgerAlertDTO;
import com.invoice.approval.repo.SubledgerRepo;

@Service
public class SubledgerAlertService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubledgerAlertService.class);

    @Autowired
    private SubledgerRepo subledgerRepo;

    @Autowired
    private EmailServiceAuto emailServiceAuto;

    @Value("${alert.recipient.email:jayabalan.guru@uniworld-logistics.com}")
    private String alertRecipientEmail;

    @Value("${app.base.url:http://202.21.34.216:8091}")
    private String baseUrl;
    
    @Autowired
    private EmailAlertHistoryService emailAlertHistoryService;

    // Add a class-level variable to store the last processed data for reference
    private List<SubledgerAlertDTO> lastProcessedSubledgers = new ArrayList<>();
   
    private Map<String, List<SubledgerAlertDTO>> groupSubledgersByRecipients(List<SubledgerAlertDTO> subledgers) {
        Map<String, List<SubledgerAlertDTO>> grouped = new HashMap<>();
        
        for (SubledgerAlertDTO subledger : subledgers) {
            // Process mailid (primary email)
            String mailid = subledger.getMailid();
            if (mailid != null && !mailid.trim().isEmpty()) {
                mailid = mailid.trim().toLowerCase();
                if (isValidEmail(mailid)) {
                    if (!grouped.containsKey(mailid)) {
                        grouped.put(mailid, new ArrayList<>());
                    }
                    grouped.get(mailid).add(subledger);
                } else {
                    LOGGER.warn("Skipping invalid mailid: {} for subledger {}", mailid, subledger.getSubledgerCode());
                }
            }
            
            // Process ccmail (additional recipients)
            String ccmail = subledger.getCcmail();
            if (ccmail != null && !ccmail.trim().isEmpty()) {
                // Split by commas/semicolons if multiple emails
                String[] ccEmails = ccmail.split("[,\\s;]+");
                for (String ccEmail : ccEmails) {
                    String trimmedCcEmail = ccEmail.trim().toLowerCase();
                    if (!trimmedCcEmail.isEmpty() && isValidEmail(trimmedCcEmail)) {
                        if (!grouped.containsKey(trimmedCcEmail)) {
                            grouped.put(trimmedCcEmail, new ArrayList<>());
                        }
                        // Add the subledger to this CC recipient's list
                        grouped.get(trimmedCcEmail).add(subledger);
                    }
                }
            }
        }
        
        return grouped;
    }
    
    private Map<String, List<SubledgerAlertDTO>> groupSubledgersBySalesperson(List<SubledgerAlertDTO> subledgers) {
        Map<String, List<SubledgerAlertDTO>> grouped = new HashMap<>();
        
        for (SubledgerAlertDTO subledger : subledgers) {
            String salespersonEmail = subledger.getMailid();
            
            if (salespersonEmail == null || salespersonEmail.trim().isEmpty()) {
                LOGGER.warn("Skipping subledger {} - no email address", subledger.getSubledgerCode());
                continue;
            }
            
            // Clean and validate email
            salespersonEmail = salespersonEmail.trim().toLowerCase();
            
            // Simple email validation
            if (!isValidEmail(salespersonEmail)) {
                LOGGER.warn("Skipping subledger {} - invalid email format: {}", 
                           subledger.getSubledgerCode(), salespersonEmail);
                continue;
            }
            
            if (!grouped.containsKey(salespersonEmail)) {
                grouped.put(salespersonEmail, new ArrayList<>());
            }
            grouped.get(salespersonEmail).add(subledger);
        }
        
        return grouped;
    }

    // Add email validation method
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email pattern check
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailPattern);
    }
    
    private String formatSummaryEmail(List<SubledgerAlertDTO> alertSubledgers) {
        String currentDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Calculate counts based on priority ranges
        long criticalCount = alertSubledgers.stream()
                .filter(s -> s.getPercentage() != null && s.getPercentage().intValue() >= 150)
                .count();
        long highCount = alertSubledgers.stream()
                .filter(s -> s.getPercentage() != null && 
                           s.getPercentage().intValue() >= 120 && s.getPercentage().intValue() < 150)
                .count();
        long mediumCount = alertSubledgers.stream()
                .filter(s -> s.getPercentage() != null && 
                           s.getPercentage().intValue() >= 100 && s.getPercentage().intValue() < 120)
                .count();
        long lowCount = alertSubledgers.stream()
                .filter(s -> s.getPercentage() != null && 
                           s.getPercentage().intValue() >= 80 && s.getPercentage().intValue() < 100)
                .count();

        // Group by salesperson for summary
        Map<String, Long> customersPerSalesperson = new HashMap<>();
        for (SubledgerAlertDTO subledger : alertSubledgers) {
            String salesperson = getSafeString(subledger.getSalesperson());
            customersPerSalesperson.put(salesperson, customersPerSalesperson.getOrDefault(salesperson, 0L) + 1);
        }

        StringBuilder email = new StringBuilder();

        email.append("<!DOCTYPE html>")
        .append("<html lang='en'>")
        .append("<head>")
        .append("<style>")
        .append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }")
        .append(".container { max-width: 1000px; margin: auto; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }")
        .append(".header { background: #34495e; color: white; padding: 25px; text-align: center; border-radius: 8px 8px 0 0; }")
        .append(".content { padding: 25px; }")
        .append(".summary { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 25px; border: 1px solid #e0e0e0; }")
        .append(".summary h3 { margin-top: 0; color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }")
        .append(".summary-table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 16px; }")
        .append(".summary-table th { background: #2c3e50; color: white; padding: 15px; text-align: center; font-weight: bold; border: 1px solid #ddd; }")
        .append(".summary-table td { padding: 20px 15px; text-align: center; border: 1px solid #ddd; font-size: 24px; font-weight: bold; }")
        .append(".summary-table tr:nth-child(even) { background: #f9f9f9; }")
        .append(".total-alerts { background: #ecf0f1 !important; }")
        .append(".critical { background: #ffebee !important; color: #d32f2f; }")
        .append(".high { background: #fff3e0 !important; color: #f57c00; }")
        .append(".medium { background: #e8f5e8 !important; color: #388e3c; }")
        .append(".low { background: #f1f8e9 !important; color: #689f38; }")
        .append(".alert-table { width: 100%; border-collapse: collapse; font-size: 14px; margin-top: 20px; }")
        .append(".alert-table th { background: #1976d2; color: white; padding: 12px 8px; text-align: left; }")
        .append(".alert-table td { padding: 10px 8px; border-bottom: 1px solid #ddd; }")
        .append(".priority-critical { background: #ffebee !important; }")
        .append(".priority-high { background: #fff3e0 !important; }")
        .append(".priority-medium { background: #e8f5e8 !important; }")
        .append(".priority-low { background: #f1f8e9 !important; }")
        .append(".serial-no { text-align: center; font-weight: bold; color: #555; }")
        .append(".stats-label { font-size: 14px; color: #7f8c8d; margin-top: 5px; }")
        .append("</style>")
        .append("</head>")
        .append("<body>")
        .append("<div class='container'>")
        .append("<div class='header'>")
        .append("<h1>🚨 Credit Limit Alert Summary</h1>")
        .append("<p>Date: ").append(currentDateTime).append("</p>")
        .append("</div>")
        .append("<div class='content'>")
        .append("<h2>Credit Limit Alert Summary</h2>")
        .append("<p>Summary of all customers with high credit utilization:</p>")

        // Summary section - Matching the image format
        .append("<div class='summary'>")
        .append("<h3>Quick Summary</h3>")
        .append("<table class='summary-table'>")
        .append("<thead>")
        .append("<tr>")
        .append("<th>Total Alerts</th>")
        .append("<th>Critical</th>")
        .append("<th>High</th>")
        .append("<th>Medium</th>")
        .append("<th>Low</th>")
        .append("</tr>")
        .append("</thead>")
        .append("<tbody>")
        .append("<tr>")
        .append("<td class='total-alerts'>").append(alertSubledgers.size()).append("<div class='stats-label'>Total Alerts</div></td>")
        .append("<td class='critical'>").append(criticalCount).append("<div class='stats-label'>>150%</div></td>")
        .append("<td class='high'>").append(highCount).append("<div class='stats-label'>120-150%</div></td>")
        .append("<td class='medium'>").append(mediumCount).append("<div class='stats-label'>100-120%</div></td>")
        .append("<td class='low'>").append(lowCount).append("<div class='stats-label'>80-100%</div></td>")
        .append("</tr>")
        .append("</tbody>")
        .append("</table>")
        .append("</div>")

        // Salesperson breakdown
        .append("<h3>Alerts by Salesperson</h3>")
        .append("<table class='alert-table'>")
        .append("<thead>")
        .append("<tr>")
        .append("<th>Salesperson</th>")
        .append("<th>Customer Count</th>")
        .append("</tr>")
        .append("</thead>")
        .append("<tbody>");
        
        for (Map.Entry<String, Long> entry : customersPerSalesperson.entrySet()) {
            email.append("<tr>")
            .append("<td>").append(entry.getKey()).append("</td>")
            .append("<td>").append(entry.getValue()).append("</td>")
            .append("</tr>");
        }
        
        email.append("</tbody>")
        .append("</table>")
        
        .append("<p style='margin-top: 20px; color: #666;'>")
        .append("<strong>Note:</strong> Individual alerts have been sent to respective salespersons.")
        .append("</p>")
        .append("<hr style='border: none; border-top: 1px solid #ddd; margin: 25px 0;'>")
        .append("<p style='font-size: 12px; color: #888; text-align: center;'>")
        .append("This is an automated alert from the Credit Monitoring System. Do not reply to this email.")
        .append("</p>")
        .append("</div>")
        .append("</div>")
        .append("</body>")
        .append("</html>");

        return email.toString();
    }

//    // Add this helper method for utilization color
    private String getUtilizationColor(double utilization) {
        if (utilization >= 150) return "#d32f2f";
        if (utilization >= 120) return "#f57c00";
        if (utilization >= 100) return "#03A9F4";
        return "#689f38";
    }

    // Add this method to format currency
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "₹0";
        try {
            return String.format("₹%,.2f", amount);
        } catch (Exception e) {
            return "₹" + amount.toString();
        }
    }
    public void checkAndSendSubledgerAlerts() {
        // Step 1: Get all subledgers with null safety
        List<SubledgerAlertDTO> highPercentageSubledgers = subledgerRepo.getSubledgersWithHighPercentage();
        
        LOGGER.info("Found {} subledgers with high credit utilization (before filtering)", highPercentageSubledgers.size());
        
        // Step 2: Apply comprehensive filtering with logging
        List<SubledgerAlertDTO> filteredSubledgers = highPercentageSubledgers.stream()
                .filter(s -> s != null)
                .filter(s -> {
                    // Keep if has mailid OR ccmail
                    boolean hasMailid = s.getMailid() != null && !s.getMailid().trim().isEmpty();
                    boolean hasCcmail = s.getCcmail() != null && !s.getCcmail().trim().isEmpty();
                    return hasMailid || hasCcmail;
                })
                .filter(s -> s.getSubledgerCode() != null && !s.getSubledgerCode().trim().isEmpty())
                .filter(s -> s.getPercentage() != null && s.getPercentage().compareTo(new BigDecimal("80")) >= 0)
                .collect(Collectors.toList());
        
        LOGGER.info("After filtering: {} valid subledgers for alerts", filteredSubledgers.size());
        
        // Store for reference (optional)
        lastProcessedSubledgers = new ArrayList<>(filteredSubledgers);
        
        // Step 3: Group by ALL recipients (mailid + ccmail)
        Map<String, List<SubledgerAlertDTO>> subledgersByRecipient = groupSubledgersByRecipients(filteredSubledgers);
        
        LOGGER.info("Grouped into {} recipients (mailid + ccmail)", subledgersByRecipient.size());
        
        // Step 4: Send emails to ALL recipients
        for (Map.Entry<String, List<SubledgerAlertDTO>> entry : subledgersByRecipient.entrySet()) {
            String recipientEmail = entry.getKey();
            List<SubledgerAlertDTO> recipientSubledgers = entry.getValue();
            
            if (recipientEmail != null && !recipientEmail.trim().isEmpty() && 
                recipientSubledgers != null && !recipientSubledgers.isEmpty()) {
                
                LOGGER.info("Processing email for recipient: {} with {} customers", 
                    recipientEmail, recipientSubledgers.size());
                
                try {
                    emailServiceAuto.sendSalespersonAlertEmail(recipientEmail, recipientSubledgers);
                    
                    // SAVE SUCCESS HISTORY
                    // Determine if this recipient is from mailid or ccmail
                    boolean isFromMailid = recipientSubledgers.stream()
                        .anyMatch(s -> s.getMailid() != null && s.getMailid().trim().equalsIgnoreCase(recipientEmail));
                    String recipientType = isFromMailid ? "MAILID" : "CCMAIL";
                    
                    // Get all unique CC emails for this recipient as List<String>
                    List<String> allCCEmails = new ArrayList<>();
                    for (SubledgerAlertDTO subledger : recipientSubledgers) {
                        if (subledger.getCcmail() != null && !subledger.getCcmail().trim().isEmpty()) {
                            String[] ccEmails = subledger.getCcmail().split("[,\\s;]+");
                            for (String cc : ccEmails) {
                                String trimmedCc = cc.trim().toLowerCase();
                                if (!trimmedCc.equalsIgnoreCase(recipientEmail) && 
                                    isValidEmail(trimmedCc)) {
                                    allCCEmails.add(trimmedCc);
                                }
                            }
                        }
                    }
                    
                    // Remove duplicates
                    allCCEmails = allCCEmails.stream().distinct().collect(Collectors.toList());
                    
                    // CORRECTED CALL - pass List<String> instead of String
                    emailAlertHistoryService.saveConsolidatedAlertHistory(
                        recipientEmail,                  // String toEmail
                        allCCEmails.isEmpty() ? null : allCCEmails,  // List<String> ccEmails (or null)
                        recipientSubledgers.get(0).getSalesperson(), // String salespersonName
                        recipientSubledgers,             // List<SubledgerAlertDTO> subledgers
                        true,                            // boolean success
                        "Recipient type: " + recipientType // String errorMessage/notes
                    );
                    
                    LOGGER.info("✅ Sent consolidated alert email to: {} ({} customers)", 
                        recipientEmail, recipientSubledgers.size());
                        
                } catch (Exception e) {
                    LOGGER.error("❌ Failed to send email to {}: {}", recipientEmail, e.getMessage());
                    
                    // SAVE FAILED HISTORY
                    try {
                        // CORRECTED CALL - pass null for ccEmails
                        emailAlertHistoryService.saveConsolidatedAlertHistory(
                            recipientEmail,
                            null,  // List<String> ccEmails - null for failed emails
                            recipientSubledgers.get(0).getSalesperson(),
                            recipientSubledgers,
                            false,  // boolean success - false for failed
                            "Failed: " + e.getMessage()  // String errorMessage
                        );
                    } catch (Exception historyEx) {
                        LOGGER.error("Failed to save history for failed email: {}", historyEx.getMessage());
                    }
                }
            }
        }
        

        
        // Step 6: Log summary
        if (!filteredSubledgers.isEmpty()) {
            LOGGER.info("✅ Alerts processed. Total: {} customers, {} recipients notified. Summary sent to admin.", 
                filteredSubledgers.size(), subledgersByRecipient.size());
        } else {
            LOGGER.info("ℹ️ No valid subledgers found for alerts");
        }
    }
    
    
  

//



    private String getSafeString(String value) {
        return value != null ? value : "N/A";
    }

    private String getSafeBigDecimal(BigDecimal value) {
        return value != null ? value.toString() : "0";
    }

    private String getSafeInteger(Integer value) {
        return value != null ? value.toString() : "0";
    }

    @Scheduled(cron = "0 30 08 * * ?")
    public void scheduledSubledgerAlertCheck() {
        LOGGER.info("Starting scheduled subledger alert check");
        checkAndSendSubledgerAlerts();
    }
}