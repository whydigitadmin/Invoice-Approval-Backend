package com.invoice.approval.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.invoice.approval.dto.SubledgerAlertDTO;
import com.invoice.approval.repo.EmployeeMasterRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceAuto {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmployeeMasterRepo employeeRepository;

    @Autowired
    private RestTemplate restTemplate;

    private String watchDirectory;
    
    @Value("${email.bcc.address:}")
    private String bccAddress;
    
    @Value("${whatsapp.service.url:http://localhost:3000/send-whatsapp}")
    private String whatsappServiceUrl;
    
    @Value("${whatsapp.enabled:false}")
    private boolean whatsappEnabled;
    
    @Value("${app.base.url:http://202.21.34.218:8091}")
    private String baseUrl;

    @Value("${pdf.directory.path:D:/Desktop/Email}")
    public void setWatchDirectory(String path) {
        this.watchDirectory = path;
    }

    public String getWatchDirectory() {
        return this.watchDirectory;
    }
    
    public List<Map<String, String>> getAvailableFiles() {
        List<Map<String, String>> fileList = new ArrayList<>();
        Path directory = Paths.get(watchDirectory);
        
        try {
            if (!Files.exists(directory)) {
                throw new RuntimeException("Directory not found: " + directory);
            }

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.txt")) {
                for (Path textFile : stream) {
                    try {
                        String employeeCode = getBaseName(textFile.getFileName().toString());
                        Path pdfFile = directory.resolve(employeeCode + ".pdf");

                        if (Files.exists(pdfFile)) {
                            Optional<String> emailOpt = employeeRepository.findEmailByCode(employeeCode);
                            
                            Map<String, String> fileInfo = new HashMap<>();
                            fileInfo.put("employeeCode", employeeCode);
                            fileInfo.put("textFileName", textFile.getFileName().toString());
                            fileInfo.put("pdfFileName", pdfFile.getFileName().toString());
                            emailOpt.ifPresent(email -> fileInfo.put("email", email));
                            
                            fileList.add(fileInfo);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing file " + textFile + ": " + e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading directory: " + e.getMessage(), e);
        }
        
        return fileList;
    }
    
    public String getFileContent(String filename) throws IOException {
        Path file = Paths.get(watchDirectory).resolve(filename);
        return new String(Files.readAllBytes(file));
    }

    public void sendSelectedEmails(List<String> employeeCodes, String bccAddress) {
        Path directory = Paths.get(watchDirectory);
        Path backupDir = directory.resolve("backup");
        
        try {
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }

            List<Path> processedFiles = new ArrayList<>();

            for (String employeeCode : employeeCodes) {
                Path textFile = directory.resolve(employeeCode + ".txt");
                Path pdfFile = directory.resolve(employeeCode + ".pdf");

                if (Files.exists(textFile) && Files.exists(pdfFile)) {
                    Optional<String> emailOpt = employeeRepository.findEmailByCode(employeeCode);
                    
                    if (emailOpt.isPresent()) {
                        if (sendEmailWithAttachments(
                            emailOpt.get(),
                            "Documents for " + employeeCode,
                            textFile,
                            pdfFile,
                            bccAddress  
                        )) {
                            processedFiles.add(textFile);
                            processedFiles.add(pdfFile);
                        }
                    }
                }
            }

            moveFilesToBackup(processedFiles, backupDir);
        } catch (Exception e) {
            throw new RuntimeException("Error sending selected emails", e);
        }
    }
    
    public void sendSalespersonAlertEmail(String toEmail, String subject, String emailContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        
        if (StringUtils.hasText(bccAddress)) {
            helper.setBcc(bccAddress);
        }
        
        helper.setText(emailContent, true);
        
        mailSender.send(message);
        log.info("Salesperson dashboard email sent to: {}", toEmail);
    }

    private String formatSalespersonEmail(List<SubledgerAlertDTO> subledgers, String priority) {
        StringBuilder email = new StringBuilder();
        
        email.append("<div style='padding:10px;margin-bottom:20px;border-radius:5px;");
        
        switch(priority) {
            case "HIGH":
                email.append("background-color:#ffebee;color:#d32f2f;border-left:4px solid #d32f2f;'>");
                email.append("<strong>🟥 HIGH PRIORITY ALERT</strong> - Customers with 120-150% credit utilization</div>");
                break;
            case "MEDIUM":
                email.append("background-color:#fff3e0;color:#f57c00;border-left:4px solid #f57c00;'>");
                email.append("<strong>🟧 MEDIUM PRIORITY ALERT</strong> - Customers with 100-120% credit utilization</div>");
                break;
            case "LOW":
                email.append("background-color:#e8f5e8;color:#388e3c;border-left:4px solid #388e3c;'>");
                email.append("<strong>🟩 LOW PRIORITY ALERT</strong> - Customers with 80-100% credit utilization</div>");
                break;
        }
        
        return email.toString();
    }

    private boolean sendEmailWithAttachments(String toEmail, String subject, 
                                          Path textFile, Path pdfFile,String bccAddress) 
                                          throws MessagingException, IOException {
        log.info("Preparing email to: {}", toEmail);
        
        try {
            String textContent = new String(Files.readAllBytes(textFile));
            byte[] pdfContent = Files.readAllBytes(pdfFile);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("gjayabalan08@gmail.com");
            helper.setTo(toEmail);
            if (StringUtils.hasText(bccAddress)) {
                helper.setBcc(bccAddress);
            }
            helper.setSubject(subject);
            helper.setText(buildEmailBody(textContent, toEmail.split("@")[0]), true);
            
            helper.addAttachment(pdfFile.getFileName().toString(), 
                              new ByteArrayResource(pdfContent));

            mailSender.send(message);
            log.info("Successfully sent email to: {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    private void moveFilesToBackup(List<Path> files, Path backupDir) throws IOException {
        if (files.isEmpty()) {
            log.info("No files to move to backup");
            return;
        }

        Map<String, List<Path>> filesByEmployee = files.stream()
            .collect(Collectors.groupingBy(
                file -> getBaseName(file.getFileName().toString())
            ));

        for (Map.Entry<String, List<Path>> entry : filesByEmployee.entrySet()) {
            String employeeCode = entry.getKey();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFolderName = employeeCode + "_" + timestamp;
            
            Path employeeBackupDir = backupDir.resolve(backupFolderName);
            Files.createDirectories(employeeBackupDir);

            for (Path file : entry.getValue()) {
                try {
                    Path target = employeeBackupDir.resolve(file.getFileName());
                    Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
                    log.info("Moved {} to backup: {}", file.getFileName(), target);
                } catch (IOException e) {
                    log.error("Failed to move {} to backup: {}", file, e.getMessage());
                }
            }
        }
    }
    
    public void sendSummaryAlertEmail(String toEmail, String subject, String emailContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        
        if (StringUtils.hasText(bccAddress)) {
            helper.setBcc(bccAddress);
        }
        
        helper.setText(emailContent, true);
        
        mailSender.send(message);
        log.info("Summary alert email sent to: {}", toEmail);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "₹0";
        try {
            return String.format("₹%,.2f", amount);
        } catch (Exception e) {
            return "₹" + amount.toString();
        }
    }
    
    private String buildEmailBody(String content, String name) {
        return "<!DOCTYPE html>" +
               "<html><head><style>" +
               "body { font-family: Arial, sans-serif; }" +
               ".content { background: #f5f5f5; padding: 15px; border-radius: 5px; }" +
               "</style></head>" +
               "<body>" +
               "<h2>Hello, " + name + "!</h2>" +
               "<div class='content'>" + content + "</div>" +
               "<p>Please find your document attached.</p>" +
               "</body></html>";
    }

    private String getBaseName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
    
    public void sendSubledgerAlertEmail(SubledgerAlertDTO subledger) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(subledger.getMailid());
        helper.setSubject("Credit Limit Alert: " + subledger.getSubledgerCode() + " - " + subledger.getSubledgerName());
        
        if (StringUtils.hasText(bccAddress)) {
            helper.setBcc(bccAddress);
        }
        
        String body = buildSubledgerAlertEmailBody(subledger);
        helper.setText(body, true);
        
        mailSender.send(message);
        log.info("Subledger alert email sent to: {}", subledger.getMailid());
    }
    
    private String buildSubledgerAlertEmailBody(SubledgerAlertDTO subledger) {
        String priority = "";
        String priorityColor = "";
        double utilization = subledger.getPercentage() != null ? subledger.getPercentage().doubleValue() : 0;
        
        if (utilization >= 150) {
            priority = "CRITICAL";
            priorityColor = "#D32F2F";
        } else if (utilization >= 120) {
            priority = "HIGH";
            priorityColor = "#FF5722";
        } else if (utilization >= 100) {
            priority = "MEDIUM";
            priorityColor = "#FF9800";
        } else {
            priority = "LOW";
            priorityColor = "#4CAF50";
        }
        
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
               "<p>Credit utilization has reached " + String.format("%.1f", utilization) + "% for customer: <strong>" + 
               subledger.getSubledgerName() + "</strong></p>" +
               "</div>" +
               "<table class='details-table'>" +
               "<tr><td><strong>Customer Code:</strong></td><td>" + getSafeString(subledger.getSubledgerCode()) + "</td></tr>" +
               "<tr><td><strong>Customer Name:</strong></td><td>" + getSafeString(subledger.getSubledgerName()) + "</td></tr>" +
               "<tr><td><strong>Customer Name:</strong></td><td>" + getSafeString(subledger.getCategory()) + "</td></tr>" +
               "<tr><td><strong>Control Office:</strong></td><td>" + getSafeString(subledger.getCtrlOffice()) + "</td></tr>" +
               "<tr><td><strong>Salesperson:</strong></td><td>" + getSafeString(subledger.getSalesperson()) + "</td></tr>" +
               "<tr><td><strong>Credit Limit:</strong></td><td>₹" + getSafeBigDecimal(subledger.getCreditLimit()) + "</td></tr>" +
               "<tr><td><strong>Credit Days:</strong></td><td>" + getSafeInteger(subledger.getCreditDays()) + " days</td></tr>" +
               "<tr><td><strong>Total Due:</strong></td><td>₹" + getSafeBigDecimal(subledger.getTotdue()) + "</td></tr>" +
               "<tr><td><strong>Utilization %:</strong></td><td><strong>" + String.format("%.1f", utilization) + "%</strong></td></tr>" +
               "</table>" +
               "<p><em>Please review this account and take appropriate action.</em></p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }

    private String getSafeString(String value) {
        return value != null ? value : "N/A";
    }

    private String getSafeBigDecimal(BigDecimal value) {
        return value != null ? value.toString() : "0";
    }

    private String getSafeInteger(Integer value) {
        return value != null ? value.toString() : "0";
    }

    private String getSafeEmployeeName(List<SubledgerAlertDTO> subledgers) {
        for (SubledgerAlertDTO subledger : subledgers) {
            if (subledger != null && subledger.getEmployee() != null && !subledger.getEmployee().trim().isEmpty()) {
                return subledger.getEmployee();
            }
        }
        return "All";
    }
    
 // In SubledgerAlertService, call this:
    // In SubledgerAlertService, call this:
  

    // FIXED METHOD: This was line 509 with the URLEncoder issue
    private String buildSalespersonAlertEmailBody(String employeeName, List<SubledgerAlertDTO> subledgers, 
            String salespersonEmail, long criticalCount, long highCount, 
            long mediumCount, long lowCount, List<String> ccEmails) {
        
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));

        String safeEmployeeName = employeeName != null ? employeeName : "Employee";
        String safeSalespersonEmail = salespersonEmail != null ? salespersonEmail : "";

        // FIXED: Use old Java compatible URL encoding
        String encodedEmail = "";
        String encodedName = "";
        try {
            encodedEmail = URLEncoder.encode(safeSalespersonEmail, "UTF-8");
            encodedName = URLEncoder.encode(safeEmployeeName, "UTF-8");
        } catch (Exception e) {
            log.error("Error encoding URL parameters: {}", e.getMessage());
            encodedEmail = safeSalespersonEmail.replace(" ", "%20");
            encodedName = safeEmployeeName.replace(" ", "%20");
        }

        String downloadLink = baseUrl + "/api/alerts/download-excel?salespersonEmail=" + 
                encodedEmail + "&employeeName=" + encodedName;

        StringBuilder email = new StringBuilder();

        email.append("<!DOCTYPE html>")
        .append("<html lang='en'>")
        .append("<head>")
        .append("<style>")
        .append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }")
        .append(".container { max-width: 1000px; margin: auto; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }")
        .append(".header { background: #34495e; color: white; padding: 25px; text-align: center; border-radius: 8px 8px 0 0; }")
        .append(".content { padding: 25px; }")
        .append(".summary { background: #f8f9fa; padding: 15px; border-radius: 6px; margin-bottom: 20px; border-left: 4px solid #1976d2; }")
        .append(".cc-info { background: #e8f4fd; padding: 10px; border-radius: 5px; margin-bottom: 15px; font-size: 14px; color: #1976d2; border-left: 4px solid #1976d2; }")
        .append(".alert-table { width: 100%; border-collapse: collapse; font-size: 14px; }")
        .append(".alert-table th { background: #1976d2; color: white; padding: 12px 8px; text-align: left; }")
        .append(".alert-table td { padding: 10px 8px; border-bottom: 1px solid #ddd; }")
        .append(".priority-critical { background: #ffebee !important; }")
        .append(".priority-high { background: #fff3e0 !important; }")
        .append(".priority-medium { background: #e8f5e8 !important; }")
        .append(".priority-low { background: #f1f8e9 !important; }")
        .append(".priority-critical-text { color: #d32f2f; font-weight: bold; }")
        .append(".priority-high-text { color: #f57c00; font-weight: bold; }")
        .append(".priority-medium-text { color: #03A9F4; font-weight: bold; }")
        .append(".priority-low-text { color: #689f38; font-weight: bold; }")
        .append(".download-btn { background: #28a745; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block; margin: 15px 0; }")
        .append(".serial-no { text-align: center; font-weight: bold; color: #555; }")
        .append("</style>")
        .append("</head>")
        .append("<body>")
        .append("<div class='container'>")
        .append("<div class='header'>")
        .append("<h1>🚨 Credit Limit Alerts</h1>")
        .append("<p>Date: ").append(currentDateTime).append("</p>")
        .append("</div>")
        .append("<div class='content'>")
        .append("<h2>Dear Mr./Ms.").append(getSafeString(employeeName)).append(",</h2>")
        .append("<p>The following customers under your responsibility have reached 80% or more of their credit limits:</p>");
        
        if (ccEmails != null && !ccEmails.isEmpty()) {
            email.append("<div class='cc-info'>")
            .append("<strong>📧 This email has been copied to:</strong> ")
            .append(String.join(", ", ccEmails))
            .append("</div>");
        }

        email.append("<div class='summary'>")
        .append("<h3 style='margin-top: 0; color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px;'>📊 Quick Summary</h3>")
        .append("<table style='width:100%; border-collapse: collapse; margin-top: 15px; border-radius: 6px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>")

        .append("<tr>")
        .append("<th style='border:1px solid #e0e0e0; padding:10px 15px; text-align:center; background:#2c3e50; color:white; font-size:16px;'>")
        .append("<div style='display:flex; align-items:center; justify-content:center;'>")
        .append("<span style='font-size:24px; margin-right:8px;'>📊</span>")
        .append("<div>")
        .append("<div style='font-weight:normal; margin-bottom:2px; font-size:14px;'>Total Alerts</div>")
        .append("<div style='font-size:28px; font-weight:bold; line-height:1.2;'>").append(subledgers.size()).append("</div>")
        .append("</div>")
        .append("</div>")
        .append("<div style='font-size:11px; opacity:0.9; margin-top:3px;'>Total customers</div>")
        .append("</th>")
        .append("<th style='border:1px solid #e0e0e0; padding:10px 15px; text-align:center; background:#d32f2f; color:white; font-size:16px;'>")
        .append("<div style='display:flex; align-items:center; justify-content:center;'>")
        .append("<span style='font-size:24px; margin-right:8px;'>🔥</span>")
        .append("<div>")
        .append("<div style='font-weight:normal; margin-bottom:2px; font-size:14px;'>Critical</div>")
        .append("<div style='font-size:28px; font-weight:bold; line-height:1.2;'>").append(criticalCount).append("</div>")
        .append("</div>")
        .append("</div>")
        .append("<div style='font-size:11px; opacity:0.9; margin-top:3px;'>(≥150%)</div>")
        .append("</th>")
        .append("<th style='border:1px solid #e0e0e0; padding:10px 15px; text-align:center; background:#f57c00; color:white; font-size:16px;'>")
        .append("<div style='display:flex; align-items:center; justify-content:center;'>")
        .append("<span style='font-size:24px; margin-right:8px;'>⚠</span>")
        .append("<div>")
        .append("<div style='font-weight:normal; margin-bottom:2px; font-size:14px;'>High</div>")
        .append("<div style='font-size:28px; font-weight:bold; line-height:1.2;'>").append(highCount).append("</div>")
        .append("</div>")
        .append("</div>")
        .append("<div style='font-size:11px; opacity:0.9; margin-top:3px;'>(120-150%)</div>")
        .append("</th>")
        .append("<th style='border:1px solid #e0e0e0; padding:10px 15px; text-align:center; background:#03A9F4; color:white; font-size:16px;'>")
        .append("<div style='display:flex; align-items:center; justify-content:center;'>")
        .append("<span style='font-size:24px; margin-right:8px;'>⚡</span>")
        .append("<div>")
        .append("<div style='font-weight:normal; margin-bottom:2px; font-size:14px;'>Medium</div>")
        .append("<div style='font-size:28px; font-weight:bold; line-height:1.2;'>").append(mediumCount).append("</div>")
        .append("</div>")
        .append("</div>")
        .append("<div style='font-size:11px; opacity:0.9; margin-top:3px;'>(100-120%)</div>")
        .append("</th>")
        .append("<th style='border:1px solid #e0e0e0; padding:10px 15px; text-align:center; background:#689f38; color:white; font-size:16px;'>")
        .append("<div style='display:flex; align-items:center; justify-content:center;'>")
        .append("<span style='font-size:24px; margin-right:8px;'>✅</span>")
        .append("<div>")
        .append("<div style='font-weight:normal; margin-bottom:2px; font-size:14px;'>Low</div>")
        .append("<div style='font-size:28px; font-weight:bold; line-height:1.2;'>").append(lowCount).append("</div>")
        .append("</div>")
        .append("</div>")
        .append("<div style='font-size:11px; opacity:0.9; margin-top:3px;'>(80-100%)</div>")
        .append("</th>")
        .append("</tr>")
        .append("</table>")
        .append("</div>")

        .append("<div style='text-align: center; margin: 20px 0;'>")
        .append("<a href='").append(downloadLink).append("' class='download-btn'>")
        .append("📊 Download Your Customer Data (Excel)")
        .append("</a>")
        .append("<p style='font-size: 12px; color: #666;'>Download your specific customer data in Excel format</p>")
        .append("</div>")

        .append("<h3>Customer Details</h3>")
        .append("<table class='alert-table'>")
        .append("<thead>")
        .append("<tr>")
        .append("<th>S.No</th>")
        .append("<th>Priority</th>")
        .append("<th>Customer Code</th>")
        .append("<th>Customer Name</th>")
        .append("<th>Category</th>")
        .append("<th>Salesperson</th>")
        .append("<th>Control Office</th>")
        .append("<th>Credit Limit (₹)</th>")
        .append("<th>Credit Days</th>")
        .append("<th>Total Due (₹)</th>")
        .append("<th>Utilization %</th>")
        .append("</tr>")
        .append("</thead>")
        .append("<tbody>");

        int serialNo = 1;
        for (SubledgerAlertDTO subledger : subledgers) {
            if (subledger == null) continue;

            double utilization = subledger.getPercentage() != null ? subledger.getPercentage().doubleValue() : 0;
            String priorityClass = "";
            String priorityText = "";
            String prioritySpan = "";

            if (utilization >= 150) {
                priorityClass = "priority-critical";
                priorityText = "CRITICAL";
                prioritySpan = "<span class='priority-critical-text'>" + priorityText + "</span>";
            } else if (utilization >= 120) {
                priorityClass = "priority-high";
                priorityText = "HIGH";
                prioritySpan = "<span class='priority-high-text'>" + priorityText + "</span>";
            } else if (utilization >= 100) {
                priorityClass = "priority-medium";
                priorityText = "MEDIUM";
                prioritySpan = "<span class='priority-medium-text'>" + priorityText + "</span>";
            } else {
                priorityClass = "priority-low";
                priorityText = "LOW";
                prioritySpan = "<span class='priority-low-text'>" + priorityText + "</span>";
            }

            email.append("<tr class='").append(priorityClass).append("'>")
            .append("<td class='serial-no'>").append(serialNo++).append("</td>")
            .append("<td>").append(prioritySpan).append("</td>")
            .append("<td>").append(getSafeString(subledger.getSubledgerCode())).append("</td>")
            .append("<td>").append(getSafeString(subledger.getSubledgerName())).append("</td>")
            .append("<td>").append(getSafeString(subledger.getCategory())).append("</td>")
            .append("<td>").append(getSafeString(subledger.getSalesperson())).append("</td>")
            .append("<td>").append(getSafeString(subledger.getCtrlOffice())).append("</td>")
            .append("<td style='text-align: right;'>").append(formatCurrency(subledger.getCreditLimit())).append("</td>")
            .append("<td style='text-align: center;'>").append(getSafeInteger(subledger.getCreditDays())).append("</td>")
            .append("<td style='text-align: right;'>").append(formatCurrency(subledger.getTotdue())).append("</td>")
            .append("<td style='text-align: center; font-weight: bold;'>").append(String.format("%.1f", utilization)).append("%</td>")
            .append("</tr>");
        }

        email.append("</tbody>")
        .append("</table>")
        .append("<p style='margin-top: 20px; color: #666;'>")
        .append("<strong>Note:</strong> Please review these accounts and take appropriate action to manage credit exposure.")
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

    public void sendSalespersonAlertEmailWithCC(String salespersonEmail, List<SubledgerAlertDTO> subledgers, 
                                               List<String> ccEmails) throws MessagingException {
        if (subledgers == null || subledgers.isEmpty()) {
            log.warn("No subledgers provided for salesperson: {}", salespersonEmail);
            return;
        }
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        String employeeName = getSafeEmployeeName(subledgers);
        
        long criticalCount = subledgers.stream()
                .filter(s -> s != null && s.getPercentage() != null && s.getPercentage().doubleValue() >= 150)
                .count();
        long highCount = subledgers.stream()
                .filter(s -> s != null && s.getPercentage() != null && 
                           s.getPercentage().doubleValue() >= 120 && s.getPercentage().doubleValue() < 150)
                .count();
        long mediumCount = subledgers.stream()
                .filter(s -> s != null && s.getPercentage() != null && 
                           s.getPercentage().doubleValue() >= 100 && s.getPercentage().doubleValue() < 120)
                .count();
        long lowCount = subledgers.stream()
                .filter(s -> s != null && s.getPercentage() != null && 
                           s.getPercentage().doubleValue() >= 80 && s.getPercentage().doubleValue() < 100)
                .count();
        
        helper.setFrom("gjayabalan08@gmail.com");
        helper.setTo(salespersonEmail);
        
        if (ccEmails != null && !ccEmails.isEmpty()) {
            helper.setCc(ccEmails.toArray(new String[0]));
            log.info("Adding CC emails for {}: {}", salespersonEmail, String.join(", ", ccEmails));
        }
        
        helper.setSubject("🚨 Credit Limit Alerts - Your Customers (" + subledgers.size() + " alerts)");
        
        String body = buildSalespersonAlertEmailBody(employeeName, subledgers, salespersonEmail, 
                                                    criticalCount, highCount, mediumCount, lowCount, ccEmails);
        helper.setText(body, true);
        
        mailSender.send(message);
        log.info("Salesperson alert email sent to: {} with CC: {} ({} customers)", 
                 salespersonEmail, 
                 ccEmails != null && !ccEmails.isEmpty() ? String.join(", ", ccEmails) : "None", 
                 subledgers.size());
    }

    public void sendSalespersonAlertEmail(String salespersonEmail, List<SubledgerAlertDTO> subledgers) throws MessagingException {
        sendSalespersonAlertEmailWithCC(salespersonEmail, subledgers, null);
    }
}