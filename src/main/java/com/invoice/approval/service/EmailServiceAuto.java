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
import org.springframework.core.io.ClassPathResource;
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

    @Value("${app.base.url:https://202.21.34.218:8091}")
    private String baseUrl;

    @Value("${pdf.directory.path:D:/Desktop/Email}")
    public void setWatchDirectory(String path) {
        this.watchDirectory = path;
    }

    public String getWatchDirectory() {
        return this.watchDirectory;
    }
    
    private void attachCompanyLogo(MimeMessageHelper helper) {
        try {
            ClassPathResource logo = new ClassPathResource("static/logoonly.png");
            helper.addInline("companyLogo", logo);
            log.info("✅ Logo attached successfully for email");
        } catch (Exception logoEx) {
            log.warn("⚠️ Could not attach logo: {}", logoEx.getMessage());
        }
    }



    public List<Map<String, String>> getAvailableFiles() {
        List<Map<String, String>> fileList = new ArrayList<>();
        Path directory = Paths.get(watchDirectory);
        try {
            if (!Files.exists(directory)) throw new RuntimeException("Directory not found: " + directory);
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
            if (!Files.exists(backupDir)) Files.createDirectories(backupDir);
            List<Path> processedFiles = new ArrayList<>();
            for (String employeeCode : employeeCodes) {
                Path textFile = directory.resolve(employeeCode + ".txt");
                Path pdfFile  = directory.resolve(employeeCode + ".pdf");
                if (Files.exists(textFile) && Files.exists(pdfFile)) {
                    Optional<String> emailOpt = employeeRepository.findEmailByCode(employeeCode);
                    if (emailOpt.isPresent()) {
                        if (sendEmailWithAttachments(emailOpt.get(), "Documents for " + employeeCode, textFile, pdfFile, bccAddress)) {
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
        if (StringUtils.hasText(bccAddress)) helper.setBcc(bccAddress);
        helper.setText(emailContent, true);
        mailSender.send(message);
        log.info("Salesperson dashboard email sent to: {}", toEmail);
    }

    private boolean sendEmailWithAttachments(String toEmail, String subject,
                                             Path textFile, Path pdfFile, String bccAddress)
            throws MessagingException, IOException {
        log.info("Preparing email to: {}", toEmail);
        try {
            String textContent = new String(Files.readAllBytes(textFile));
            byte[] pdfContent  = Files.readAllBytes(pdfFile);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("gjayabalan08@gmail.com");
            helper.setTo(toEmail);
            if (StringUtils.hasText(bccAddress)) helper.setBcc(bccAddress);
            helper.setSubject(subject);
            helper.setText(buildEmailBody(textContent, toEmail.split("@")[0]), true);
            helper.addAttachment(pdfFile.getFileName().toString(), new ByteArrayResource(pdfContent));
            mailSender.send(message);
            log.info("Successfully sent email to: {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    private void moveFilesToBackup(List<Path> files, Path backupDir) throws IOException {
        if (files.isEmpty()) { log.info("No files to move to backup"); return; }
        Map<String, List<Path>> filesByEmployee = files.stream()
                .collect(Collectors.groupingBy(file -> getBaseName(file.getFileName().toString())));
        for (Map.Entry<String, List<Path>> entry : filesByEmployee.entrySet()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path employeeBackupDir = backupDir.resolve(entry.getKey() + "_" + timestamp);
            Files.createDirectories(employeeBackupDir);
            for (Path file : entry.getValue()) {
                try {
                    Files.move(file, employeeBackupDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    log.info("Moved {} to backup", file.getFileName());
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
        if (StringUtils.hasText(bccAddress)) helper.setBcc(bccAddress);
        helper.setText(emailContent, true);
        mailSender.send(message);
        log.info("Summary alert email sent to: {}", toEmail);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "&#8377;0.00";
        try { return String.format("&#8377;%,.2f", amount); }
        catch (Exception e) { return "&#8377;" + amount; }
    }

    private String buildEmailBody(String content, String name) {
        return "<!DOCTYPE html><html><head><style>"
             + "body{font-family:Arial,sans-serif;}"
             + ".content{background:#f5f5f5;padding:15px;border-radius:5px;}"
             + "</style></head><body>"
             + "<h2>Hello, " + name + "!</h2>"
             + "<div class='content'>" + content + "</div>"
             + "<p>Please find your document attached.</p>"
             + "</body></html>";
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
        if (StringUtils.hasText(bccAddress)) helper.setBcc(bccAddress);
        helper.setText(buildSubledgerAlertEmailBody(subledger), true);
        mailSender.send(message);
        log.info("Subledger alert email sent to: {}", subledger.getMailid());
    }

    private String buildSubledgerAlertEmailBody(SubledgerAlertDTO subledger) {
        String priority, priorityColor;
        double utilization = subledger.getPercentage() != null ? subledger.getPercentage().doubleValue() : 0;
        if      (utilization >= 150) { priority = "CRITICAL"; priorityColor = "#D32F2F"; }
        else if (utilization >= 120) { priority = "HIGH";     priorityColor = "#FF5722"; }
        else if (utilization >= 100) { priority = "MEDIUM";   priorityColor = "#FF9800"; }
        else                         { priority = "LOW";       priorityColor = "#4CAF50"; }

        return "<!DOCTYPE html><html><head><style>"
             + "body{font-family:Arial,sans-serif;}"
             + ".header{background:#2c3e50;color:white;padding:20px;text-align:center;}"
             + ".content{padding:20px;}"
             + ".alert-box{background:#fff3cd;border:1px solid #ffeaa7;border-radius:5px;padding:15px;margin:15px 0;}"
             + ".details-table{width:100%;border-collapse:collapse;margin:15px 0;}"
             + ".details-table td{padding:8px;border-bottom:1px solid #ddd;}"
             + ".priority{color:" + priorityColor + ";font-weight:bold;}"
             + "</style></head><body>"
             + "<div class='header'><h1>Credit Limit Alert</h1></div>"
             + "<div class='content'><div class='alert-box'>"
             + "<h2 class='priority'>" + priority + " PRIORITY ALERT</h2>"
             + "<p>Credit utilization has reached " + String.format("%.1f", utilization)
             + "% for customer: <strong>" + subledger.getSubledgerName() + "</strong></p>"
             + "</div><table class='details-table'>"
             + "<tr><td><strong>Customer Code:</strong></td><td>" + getSafeString(subledger.getSubledgerCode()) + "</td></tr>"
             + "<tr><td><strong>Customer Name:</strong></td><td>" + getSafeString(subledger.getSubledgerName()) + "</td></tr>"
             + "<tr><td><strong>Category:</strong></td><td>" + getSafeString(subledger.getCategory()) + "</td></tr>"
             + "<tr><td><strong>Control Office:</strong></td><td>" + getSafeString(subledger.getCtrlOffice()) + "</td></tr>"
             + "<tr><td><strong>Salesperson:</strong></td><td>" + getSafeString(subledger.getSalesperson()) + "</td></tr>"
             + "<tr><td><strong>Credit Limit:</strong></td><td>" + formatCurrency(subledger.getCreditLimit()) + "</td></tr>"
             + "<tr><td><strong>Credit Days:</strong></td><td>" + getSafeInteger(subledger.getCreditDays()) + " days</td></tr>"
             + "<tr><td><strong>Total Due:</strong></td><td>" + formatCurrency(subledger.getTotdue()) + "</td></tr>"
             + "<tr><td><strong>Utilization %:</strong></td><td><strong>" + String.format("%.1f", utilization) + "%</strong></td></tr>"
             + "</table>"
             + "<p><em>Please review this account and take appropriate action.</em></p>"
             + "</div></body></html>";
    }

    // ─────────────────────────────────────────────────────────────
    // DONUT CHART BUILDER - UPDATED TO MATCH OUTSTANDING SERVICE
    // Outlook desktop → VML donut (sector paths)
    // Gmail/Apple Mail/Outlook 365 web → SVG stroke-dasharray donut
    // Each segment = one customer's share of total outstanding.
    // ─────────────────────────────────────────────────────────────
    private String buildAlertDonut(List<SubledgerAlertDTO> subledgers) {

        // Palette — up to 8 distinct colours + 1 "others" grey (matches outstanding service)
        String[] PALETTE = {
            "#1976D2", "#2E7D32", "#F57F17", "#C62828",
            "#6A1B9A", "#00838F", "#AD1457", "#E65100"
        };
        String OTHERS_COLOR = "#9E9E9E";

        // Aggregate totals per customer (by subledger code)
        java.util.LinkedHashMap<String, BigDecimal> customerTotals = new java.util.LinkedHashMap<>();
        java.util.LinkedHashMap<String, String>     customerNames  = new java.util.LinkedHashMap<>();
        for (SubledgerAlertDTO s : subledgers) {
            if (s == null || s.getTotdue() == null || s.getTotdue().compareTo(BigDecimal.ZERO) <= 0) continue;
            String code = s.getSubledgerCode() != null ? s.getSubledgerCode() : "?";
            customerTotals.merge(code, s.getTotdue(), BigDecimal::add);
            customerNames.putIfAbsent(code, s.getSubledgerName() != null ? s.getSubledgerName() : code);
        }

        // Sort by amount descending
        List<Map.Entry<String, BigDecimal>> sorted = new ArrayList<>(customerTotals.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        BigDecimal grandTotal = sorted.stream().map(Map.Entry::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (grandTotal.compareTo(BigDecimal.ZERO) == 0) return "";

        List<String>     segNames  = new ArrayList<>();
        List<BigDecimal> segAmts   = new ArrayList<>();
        List<String>     segColors = new ArrayList<>();
        List<String>     segCodes  = new ArrayList<>();
        BigDecimal othersTotal = BigDecimal.ZERO;
        int colorIdx = 0;

        for (Map.Entry<String, BigDecimal> e : sorted) {
            if (colorIdx < PALETTE.length) {
                segCodes.add(e.getKey());
                segNames.add(customerNames.getOrDefault(e.getKey(), e.getKey()));
                segAmts.add(e.getValue());
                segColors.add(PALETTE[colorIdx++]);
            } else {
                othersTotal = othersTotal.add(e.getValue());
            }
        }
        if (othersTotal.compareTo(BigDecimal.ZERO) > 0) {
            segCodes.add("");
            segNames.add("Others");
            segAmts.add(othersTotal);
            segColors.add(OTHERS_COLOR);
        }

        int N = segNames.size();
        if (N == 0) return "";

        // ── Proportions
        double[] proportions = new double[N];
        for (int i = 0; i < N; i++)
            proportions[i] = segAmts.get(i).divide(grandTotal, 10, java.math.RoundingMode.HALF_UP).doubleValue();

        // ── SVG circumference (r=60)
        double C = 2 * Math.PI * 60; // 376.99

        StringBuilder sb = new StringBuilder();

        // ════════════════════════════════════════════════════════════
        // 1. OUTLOOK VML donut (sector paths) - MATCHING OUTSTANDING SERVICE
        // ════════════════════════════════════════════════════════════
        sb.append("<!--[if mso]>")
          .append("<div style='width:140px;height:140px;position:relative;'>");

        int cx = 80, cy_vml = 80, ro = 72, ri = 42;
        double startDeg = -90.0;

        for (int i = 0; i < N; i++) {
            double sweep  = proportions[i] * 360.0;
            double endDeg = startDeg + sweep;

            double sRad = Math.toRadians(startDeg);
            double eRad = Math.toRadians(endDeg);

            int ox1 = (int) Math.round(cx + ro * Math.cos(sRad));
            int oy1 = (int) Math.round(cy_vml + ro * Math.sin(sRad));
            int ix2 = (int) Math.round(cx + ri * Math.cos(eRad));
            int iy2 = (int) Math.round(cy_vml + ri * Math.sin(eRad));

            long vmlStart    = Math.round(-startDeg * 65536);
            long vmlSweep    = Math.round(-sweep     * 65536);
            long vmlStartRev = Math.round(-endDeg    * 65536);
            long vmlSweepRev = Math.round( sweep     * 65536);

            String path = String.format(
                "M %d,%d AE %d,%d,%d,%d,%d,%d L %d,%d AE %d,%d,%d,%d,%d,%d X E",
                ox1, oy1,
                cx, cy_vml, ro, ro, vmlStart, vmlSweep,
                ix2, iy2,
                cx, cy_vml, ri, ri, vmlStartRev, vmlSweepRev
            );

            sb.append("<v:shape xmlns:v='urn:schemas-microsoft-com:vml'")
              .append(" style='position:absolute;top:0;left:0;width:160px;height:160px;'")
              .append(" coordsize='160,160' path='").append(path).append("'")
              .append(" fillcolor='").append(segColors.get(i)).append("' stroked='false'>")
              .append("</v:shape>");

            startDeg = endDeg;
        }

        // White centre hole — no text inside
        sb.append("<v:oval xmlns:v='urn:schemas-microsoft-com:vml'")
          .append(" style='position:absolute;top:").append(cy_vml - ri).append("px;")
          .append("left:").append(cx - ri).append("px;")
          .append("width:").append(ri * 2).append("px;height:").append(ri * 2).append("px;'")
          .append(" fillcolor='#ffffff' stroked='false'></v:oval>");

        sb.append("</div>")
          .append("<![endif]-->");

        // ════════════════════════════════════════════════════════════
        // 2. SVG donut — Gmail, Apple Mail, Outlook 365 web (MATCHING OUTSTANDING SERVICE)
        // ════════════════════════════════════════════════════════════
        sb.append("<!--[if !mso]><!-->")
          .append("<svg width='160' height='160' viewBox='0 0 160 160' xmlns='http://www.w3.org/2000/svg'>")
          .append("<circle cx='80' cy='80' r='60' fill='none' stroke='#E8ECF4' stroke-width='28'/>");

        double svgOffset = 0;
        for (int i = 0; i < N; i++) {
            double len = proportions[i] * C;
            double gap = C - len;
            sb.append(String.format(
                "<circle cx='80' cy='80' r='60' fill='none' stroke='%s' stroke-width='28'" +
                " stroke-dasharray='%.2f %.2f' stroke-dashoffset='%.2f'" +
                " transform='rotate(-90 80 80)'/>",
                segColors.get(i), len, gap, svgOffset
            ));
            svgOffset -= len;
        }

        // White hole — centre is empty (no text)
        sb.append("<circle cx='80' cy='80' r='44' fill='#ffffff'/>")
          .append("</svg>")
          .append("<!--<![endif]-->");

        // Build legend rows separately
        StringBuilder legend = new StringBuilder();
        BigDecimal gtForBar = segAmts.get(0); // largest = 100% bar
        for (int i = 0; i < N; i++) {
            int pct = (int) Math.round(proportions[i] * 100);
            int bar = gtForBar.compareTo(BigDecimal.ZERO) > 0
                    ? segAmts.get(i).multiply(new BigDecimal("100"))
                               .divide(gtForBar, 0, java.math.RoundingMode.HALF_UP).intValue()
                    : 0;
            String name = segNames.get(i);
            String code = segCodes.get(i);
            String displayName = name.length() > 22 ? name.substring(0, 20) + "&hellip;" : name;
            String codeSpan = (code != null && !code.isEmpty())
                    ? "<span style='font-family:Courier New,monospace;font-size:10px;color:#9aa3b8;margin-left:4px;'>" + code + "</span>"
                    : "";
            boolean divider = (i < N - 1);
            
            legend.append("<tr><td style='padding:5px 0;'>")
                  .append("<table cellpadding='0' cellspacing='0' border='0' width='100%'><tr>")
                  .append("<td width='12' valign='middle' style='padding-right:10px;'>")
                  .append("<div style='width:12px;height:12px;background-color:").append(segColors.get(i)).append(";'>&nbsp;</div></td>")
                  .append("<td valign='middle'>")
                  .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:12px;color:#374151;font-weight:bold;'>").append(displayName).append(codeSpan).append("</p>")
                  .append("<p style='margin:1px 0 0;font-family:Arial,sans-serif;font-size:11px;color:").append(segColors.get(i)).append(";font-weight:bold;'>").append(formatCurrency(segAmts.get(i))).append("</p>")
                  .append("</td>")
                  .append("<td align='right' valign='middle' style='padding-left:10px;'>")
                  .append("<table cellpadding='0' cellspacing='0' border='0' style='width:100px;'><tr>")
                  .append("<td style='background-color:#E8ECF4;height:6px;font-size:0;line-height:0;'>")
                  .append("<table width='").append(bar).append("%' cellpadding='0' cellspacing='0' border='0'><tr>")
                  .append("<td style='background-color:").append(segColors.get(i)).append(";height:6px;font-size:0;line-height:0;'>&nbsp;</td></tr></table>")
                  .append("</td></tr></table></td>")
                  .append("<td align='right' valign='middle' width='36' style='padding-left:6px;'>")
                  .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:11px;font-weight:bold;color:").append(segColors.get(i)).append(";'>").append(pct).append("%</p></td>")
                  .append("</tr></table></td></tr>");
            if (divider)
                legend.append("<tr><td style='border-top:1px solid #EEF1F8;font-size:0;line-height:0;'>&nbsp;</td></tr>");
        }

        return sb.toString() + "||LEGEND||" + legend.toString();
    }

    // ─────────────────────────────────────────────────────────────
    // SALESPERSON ALERT EMAIL — redesigned to match
    // CurrentOutstandingEmailService visual style:
    //   • Dark navy header with salesperson identity
    //   • Outlook-safe table layout, all inline styles
    //   • 4 KPI cards (Total / Critical / High / Medium / Low)
    //   • Priority colour-coded detail table
    //   • Download Excel button
    //   • Consistent footer
    // ─────────────────────────────────────────────────────────────
    
 // Add these helper methods to your EmailServiceAuto class

    /**
     * Gets professional initials from employee name
     */
    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "SP"; // Default for Sales Person
        }
        
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            // Single name - take first 2 letters
            return parts[0].length() >= 2 ? 
                   parts[0].substring(0, 2).toUpperCase() : 
                   parts[0].substring(0, 1).toUpperCase();
        } else {
            // Multiple names - take first letter of first and last
            String first = parts[0].length() > 0 ? parts[0].substring(0, 1) : "";
            String last = parts[parts.length - 1].length() > 0 ? 
                          parts[parts.length - 1].substring(0, 1) : "";
            return (first + last).toUpperCase();
        }
    }

    /**
     * Gets a consistent color based on employee name for the avatar
     */
    private String getAvatarColor(String name) {
        if (name == null || name.isEmpty()) {
            return "#C62828"; // Default red
        }
        
        // List of professional colors for avatars
        String[] colors = {
            "#C62828", // Red
            "#2E7D32", // Green
            "#1976D2", // Blue
            "#F57F17", // Orange
            "#6A1B9A", // Purple
            "#00838F", // Cyan
            "#AD1457", // Pink
            "#E65100", // Deep Orange
            "#283593", // Indigo
            "#00695C"  // Teal
        };
        
        // Simple hash to get consistent color for same name
        int hash = Math.abs(name.hashCode());
        return colors[hash % colors.length];
    }
    
   
    /**
     * Attaches company logo as inline image with CID reference
     * EXACT SAME APPROACH as CurrentOutstandingEmailService
     */


    private String buildSalespersonAlertEmailBody(String employeeName, List<SubledgerAlertDTO> subledgers,
                                                   String salespersonEmail,
                                                   long criticalCount, long highCount,
                                                   long mediumCount, long lowCount,
                                                   List<String> ccEmails) {

        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        String safeEmployeeName = employeeName != null ? employeeName : "Employee";
        String firstName = safeEmployeeName.contains(" ")
                ? safeEmployeeName.substring(0, safeEmployeeName.indexOf(' '))
                : safeEmployeeName;

        // Avatar initials
        String initials = java.util.Arrays.stream(safeEmployeeName.split("\\s+"))
                .filter(w -> !w.isEmpty()).limit(2)
                .map(w -> String.valueOf(w.charAt(0)).toUpperCase())
                .collect(java.util.stream.Collectors.joining());
        if (initials.isEmpty()) initials = "SP";

        // Download link
        String encodedEmail = "", encodedName = "";
        try {
            encodedEmail = URLEncoder.encode(salespersonEmail != null ? salespersonEmail : "", "UTF-8");
            encodedName  = URLEncoder.encode(safeEmployeeName, "UTF-8");
        } catch (Exception e) {
            encodedEmail = (salespersonEmail != null ? salespersonEmail : "").replace(" ", "%20");
            encodedName  = safeEmployeeName.replace(" ", "%20");
        }
        String downloadLink = baseUrl + "/api/alerts/download-excel?salespersonEmail="
                + encodedEmail + "&employeeName=" + encodedName;

        long totalAlerts = subledgers.size();

        StringBuilder h = new StringBuilder();

        // ── DOCTYPE + head (minimal — all styles are inline for Outlook)
        h.append("<!DOCTYPE html>")
         .append("<html lang='en' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:o='urn:schemas-microsoft-com:office:office'>")
         .append("<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'>")
         .append("<meta http-equiv='X-UA-Compatible' content='IE=edge'>")
         .append("<!--[if mso]><noscript><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml></noscript><![endif]-->")
         .append("<style>body,table,td,p,a,span{font-family:Arial,sans-serif;}body{margin:0;padding:0;background-color:#F0F2F5;}a{color:#1565C0;}v\\:*{behavior:url(#default#VML);display:inline-block;}</style>")
         .append("</head>")
         .append("<body style='margin:0;padding:0;background-color:#F0F2F5;'>")
         .append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='background-color:#F0F2F5;'>")
         .append("<tr><td align='center' style='padding:24px 12px;'>")
         .append("<table width='860' cellpadding='0' cellspacing='0' border='0' style='max-width:860px;background-color:#ffffff;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;'>");

     // ══ HEADER ════════════════════════════════════════════════
        h.append("<tr><td style='background-color:#0f2744;padding:32px 40px 26px 40px;'>")
        .append("<table width='100%' cellpadding='0' cellspacing='0' border='0'><tr>")
        .append("<td valign='middle'>")
        .append("<table cellpadding='0' cellspacing='0' border='0'><tr>")
        .append("<td valign='middle' style='padding-right:12px;'>")
        .append("<img src='cid:companyLogo' width='50' height='30' "
                + "style='width:50px;height:30px;object-fit:contain;display:block;border:0;' "
                + "alt='Logo' />")
         .append("</td>")
         .append("<td valign='middle'>")
         .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:14px;font-weight:bold;color:#ffffff;'>Uniworld Logistics</p>")
         .append("</td>")
         .append("</tr></table>")
         .append("</td>")
         
         // Right side: Date
         .append("<td valign='middle' align='right'>")
         .append("<table cellpadding='0' cellspacing='0' border='0'><tr>")
         .append("<td style='background-color:#1a3f6f;border:1px solid #2a5080;padding:7px 14px;'>")
         .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:11px;color:#B8CDE0;white-space:nowrap;'>&#128197;&nbsp;").append(currentDateTime).append("</p>")
         .append("</td></tr></table>")
         .append("</td>")
         .append("</tr></table>")
         
         // Second row: Title - CENTERED
         .append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-top:15px;'><tr>")
         .append("<td align='center' style='text-align:center;'>")
         .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:22px;font-weight:bold;color:#ffffff;'>Credit Limit Alert</p>")
         .append("</td>")
         .append("</tr></table>")
         
         // Divider + salesperson greeting
         .append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-top:20px;'>")
         .append("<tr><td style='border-top:1px solid #1e3a5f;padding-top:18px;'>")
         .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:18px;font-weight:bold;color:#ffffff;'>Hi Mr./Ms. ").append(safeEmployeeName).append("</p>")
         .append("</td></tr></table>")
         .append("</td></tr>");

        // ══ GREETING BAND ═════════════════════════════════════════
//        h.append("<tr><td style='background-color:#FFF3F3;padding:18px 40px;border-bottom:1px solid #FFCDD2;'>")
        h.append("<tr><td style='background-color:#EBF3FD;padding:20px 40px;border-bottom:1px solid #D0E4F7;'>")
//         .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:14px;color:#7f1d1d;line-height:1.7;'>")
        .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:14px;color:#1a3a5c;line-height:1.7;'>")
         .append("The following customers under your responsibility have reached ")
         .append("<strong>80% or more of their credit limits</strong>. ")
         .append("Please review each account and take appropriate action to manage credit exposure.")
         .append("</p></td></tr>");

        // ══ BODY ══════════════════════════════════════════════════
        h.append("<tr><td style='padding:28px 40px;background-color:#ffffff;'>");

        // ── KPI Row (5 cards)
        h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:8px;'><tr>")
        .append(alertKpi(String.valueOf(totalAlerts),  "Total Alerts",    "#1976D2", "20%", "padding-right:6px;"))
        .append(alertKpi(String.valueOf(criticalCount),"Critical (&ge;150%)", "#C62828", "20%", "padding-left:3px;padding-right:3px;", true))
        .append(alertKpi(String.valueOf(highCount),    "High (120-150%)", "#E65100", "20%", "padding-left:3px;padding-right:3px;"))
        .append(alertKpi(String.valueOf(mediumCount),  "Medium (100-120%)", "#F57F17", "20%", "padding-left:3px;padding-right:3px;"))
        .append(alertKpi(String.valueOf(lowCount),     "Low (80-100%)",   "#2E7D32", "20%", "padding-left:6px;"))
        .append("</tr></table>")
         .append("<p style='margin:0 0 24px 0;font-family:Arial,sans-serif;font-size:11px;color:#9aa3b2;'>&#9432;&nbsp; Customers are classified by credit utilization percentage.</p>");

        // ── DONUT CHART — customer-wise outstanding
        // Sort and build chart using the same subledgers list
        String donutRaw = buildAlertDonut(subledgers);
        String donutChart = "";
        String donutLegend = "";
        if (donutRaw.contains("||LEGEND||")) {
            String[] parts = donutRaw.split("\\|\\|LEGEND\\|\\|", 2);
            donutChart  = parts[0];
            donutLegend = parts.length > 1 ? parts[1] : "";
        }

        if (!donutChart.isEmpty()) {
            h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:28px;background-color:#FAFBFF;border:1px solid #E3E9FF;'>")
             .append("<tr><td style='padding:20px 24px;'>")
             .append("<p style='margin:0 0 18px 0;font-family:Arial,sans-serif;font-size:10px;color:#9aa3b8;text-transform:uppercase;letter-spacing:1.5px;'>Customer-wise Outstanding</p>")
             .append("<table width='100%' cellpadding='0' cellspacing='0' border='0'><tr>")
             .append("<td width='180' valign='middle' align='center' style='padding-right:24px;'>")
             .append(donutChart)
             .append("</td>")
             .append("<td valign='middle'><table cellpadding='0' cellspacing='0' border='0' width='100%'>")
             .append(donutLegend)
             .append("</table></td>")
             .append("</tr></table></td></tr>")
             
             // ADDED: Extra spacer row to increase table height when needed
             .append("<tr><td style='padding:0 24px 20px 24px;'>")
             .append("<table width='100%' cellpadding='0' cellspacing='0' border='0'><tr>")
             .append("<td style='height:20px; font-size:0; line-height:0;'>&nbsp;</td>")
             .append("</tr></table>")
             .append("</td></tr>")
             
             .append("</table>");
        }


        // ========== ADDED SPACER BETWEEN DONUT AND CUSTOMER ALERT DETAILS ==========
        h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0'>")
         .append("<tr><td style='height:40px; font-size:0; line-height:0;'>&nbsp;</td></tr>")
         .append("</table>");
        // ========== END SPACER ==========

        // ── CC Notice (moved after spacer)
        if (ccEmails != null && !ccEmails.isEmpty()) {
            h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:24px;'><tr>")
             .append("<td width='4' style='background-color:#1976D2;font-size:0;line-height:0;'>&nbsp;</td>")
             .append("<td style='background-color:#EEF6FF;padding:11px 16px;border:1px solid #BBDEFB;border-left:none;'>")
             .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:12px;color:#0d47a1;'>&#128231;&nbsp;<strong>CC:</strong>&nbsp;")
             .append(String.join(" &bull; ", ccEmails))
             .append(" &mdash; Your manager and the finance team have been copied on this report.")
             .append("</p></td></tr></table>");
        }

        // ── Download button (moved after spacer)
        h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:24px;'><tr>")
         .append("<td align='center' style='padding:4px 0;'>")
         .append("<table cellpadding='0' cellspacing='0' border='0'><tr>")
         .append("<td style='background-color:#2E7D32;padding:12px 28px;'>")
         .append("<a href='").append(downloadLink).append("' style='font-family:Arial,sans-serif;font-size:13px;font-weight:bold;color:#ffffff;text-decoration:none;'>")
         .append("&#128202;&nbsp; Download Customer Data (Excel)")
         .append("</a></td></tr></table>")
         .append("<p style='margin:6px 0 0;font-family:Arial,sans-serif;font-size:11px;color:#9aa3b2;'>Download your specific customer data in Excel format</p>")
         .append("</td></tr></table>");

        // ── Section header
        h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:10px;'><tr><td>")
         .append("<p style='margin:0 0 4px 0;font-family:Arial,sans-serif;font-size:14px;font-weight:bold;color:#0f2744;'>")
         .append("Customer Alert Details")
         .append("&nbsp;<span style='background-color:#FFEBEE;color:#C62828;font-family:Arial,sans-serif;font-size:11px;font-weight:bold;padding:3px 9px;'>").append(totalAlerts).append(" alerts</span></p>")
         .append("<p style='margin:4px 0 12px 0;font-family:Arial,sans-serif;font-size:12px;color:#7a8aaa;'>Review each account and take appropriate action to manage credit exposure.</p>")
         .append("</td></tr></table>");

        // ── Detail table
        h.append("<table width='100%' cellpadding='0' cellspacing='0' border='1' bordercolor='#E4E8F4' style='border-collapse:collapse;font-family:Arial,sans-serif;font-size:12px;'>")
         .append("<thead><tr style='background-color:#0f2744;'>")
         .append(th("center", "#"))
         .append(th("center", "PRIORITY"))
         .append(th("left",   "CUSTOMER CODE"))
         .append(th("left",   "CUSTOMER NAME"))
         .append(th("left",   "CATEGORY"))
         .append(th("left",   "SALESPERSON"))
         .append(th("left",   "CTRL OFFICE"))
         .append(th("right",  "CREDIT LIMIT"))
         .append(th("center", "CREDIT DAYS"))
         .append(th("right",  "TOTAL DUE"))
         .append(th("center", "UTILIZATION"))
         .append("</tr></thead><tbody>");

        int sno = 1;
        for (SubledgerAlertDTO s : subledgers) {
            if (s == null) continue;
            double util = s.getPercentage() != null ? s.getPercentage().doubleValue() : 0;

            // Row background & priority badge
            String rowBg, badgeBg, badgeColor, priorityLabel;
            if (util >= 150) {
                rowBg="#FFF5F5"; badgeBg="#FFEBEE"; badgeColor="#C62828"; priorityLabel="CRITICAL";
            } else if (util >= 120) {
                rowBg="#FFF8F5"; badgeBg="#FBE9E7"; badgeColor="#E65100"; priorityLabel="HIGH";
            } else if (util >= 100) {
                rowBg="#FFFDF5"; badgeBg="#FFF8E1"; badgeColor="#F57F17"; priorityLabel="MEDIUM";
            } else {
                rowBg="#F5FFF5"; badgeBg="#E8F5E9"; badgeColor="#2E7D32"; priorityLabel="LOW";
            }
            String cb = "1px solid #E8ECF4";

            h.append("<tr style='background-color:").append(rowBg).append(";'>")
             .append("<td style='padding:10px 8px;text-align:center;border:").append(cb).append(";color:#3730a3;font-weight:bold;font-size:11px;'>").append(sno).append("</td>")
             .append("<td style='padding:10px 8px;text-align:center;border:").append(cb).append(";'>")
               .append("<span style='background-color:").append(badgeBg).append(";color:").append(badgeColor).append(";font-family:Arial,sans-serif;font-size:10px;font-weight:bold;padding:3px 8px;'>").append(priorityLabel).append("</span></td>")
             .append("<td style='padding:10px 8px;border:").append(cb).append(";'><span style='background-color:#F1F5FF;color:#1e40af;font-family:Courier New,monospace;font-size:11px;font-weight:bold;padding:2px 6px;'>").append(getSafeString(s.getSubledgerCode())).append("</span></td>")
             .append("<td style='padding:10px 8px;border:").append(cb).append(";font-family:Arial,sans-serif;font-weight:bold;color:#1a1a2e;'>").append(getSafeString(s.getSubledgerName())).append("</td>")
             .append("<td style='padding:10px 8px;border:").append(cb).append(";color:#374151;'>").append(getSafeString(s.getCategory())).append("</td>")
             .append("<td style='padding:10px 8px;border:").append(cb).append(";color:#374151;'>").append(getSafeString(s.getSalesperson())).append("</td>")
             .append("<td style='padding:10px 8px;border:").append(cb).append(";color:#374151;'>").append(getSafeString(s.getCtrlOffice())).append("</td>")
             .append("<td style='padding:10px 8px;text-align:right;border:").append(cb).append(";color:#374151;'>").append(formatCurrency(s.getCreditLimit())).append("</td>")
             .append("<td style='padding:10px 8px;text-align:center;border:").append(cb).append(";color:#374151;'>").append(getSafeInteger(s.getCreditDays())).append("d</td>")
             .append("<td style='padding:10px 8px;text-align:right;border:").append(cb).append(";color:#1B5E20;font-weight:bold;'>").append(formatCurrency(s.getTotdue())).append("</td>")
             .append("<td style='padding:10px 8px;text-align:center;border:").append(cb).append(";'>")
               .append("<span style='background-color:").append(badgeBg).append(";color:").append(badgeColor).append(";font-family:Arial,sans-serif;font-size:11px;font-weight:bold;padding:3px 8px;'>")
               .append(String.format("%.1f", util)).append("%</span></td>")
             .append("</tr>");
            sno++;
        }
        h.append("</tbody></table>");

        // ── What to do checklist
        h
         .append("</td></tr>") // end body row

        // ══ FOOTER ════════════════════════════════════════════════
         .append("<tr><td style='background-color:#0f2744;padding:24px 40px;'>")
         .append("<table width='100%' cellpadding='0' cellspacing='0' border='0'><tr>")
         .append("<td valign='top' width='60%'>")
         .append("<p style='margin:0 0 3px 0;font-family:Arial,sans-serif;font-size:13px;font-weight:bold;color:rgba(255,255,255,0.65);'>Credit Monitoring System</p>")
         .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:12px;color:rgba(255,255,255,0.35);line-height:1.7;'>")
         .append("This is an automated alert. Please do not reply to this email.")
         .append("</p></td>")
         .append("<td valign='top' align='right' width='40%'>")
         .append("<p style='margin:0 0 3px 0;font-family:Arial,sans-serif;font-size:11px;color:rgba(255,255,255,0.35);'>Queries?</p>")
         .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:12px;'><a href='mailto:it.tech@uniworld-logistics.com' style='color:#64B5F6;text-decoration:none;'>it.tech@uniworld-logistics.com</a></p>")
         .append("</td></tr></table>")
         .append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin:16px 0 12px 0;'><tr><td style='border-top:1px solid #1e3a5f;font-size:0;line-height:0;'>&nbsp;</td></tr></table>")
         .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:11px;color:rgba(255,255,255,0.25);text-align:center;'>&copy; ")
         .append(LocalDateTime.now().getYear()).append(" Uniworld Logistics Pvt Ltd. All rights reserved.</p>")
         .append("</td></tr>")
         .append("</table></td></tr></table></body></html>");

        return h.toString();
    }

    // ─────────────────────────────────────────────────────────────
    // HTML FRAGMENT HELPERS
    // ─────────────────────────────────────────────────────────────

    /** Table header cell */
    private String th(String align, String label) {
        return "<th style='padding:11px 8px;color:#B8CDE0;font-size:10px;font-weight:bold;text-align:"
             + align + ";white-space:nowrap;border:1px solid #1a3f6f;'>" + label + "</th>";
    }

    /** KPI card — FULL SOLID background color with white text and icons */
    private String alertKpi(String value, String label, String accent, String width, String pad) {
        return alertKpi(value, label, accent, width, pad, false);
    }

    /** KPI card — FULL SOLID background color with white text and icons */
    private String alertKpi(String value, String label, String accent, String width, String pad, boolean redVariant) {
        // Determine which icon to show based on label
        String icon = "";
        if (label.contains("Total") || label.equals("Total Alerts")) {
            icon = "📊 ";
        } else if (label.contains("Critical")) {
            icon = "🔥 ";
        } else if (label.contains("High")) {
            icon = "⚠️ ";
        } else if (label.contains("Medium")) {
            icon = "⚡ ";
        } else if (label.contains("Low")) {
            icon = "✅ ";
        }
        
        return "<td width='" + width + "' valign='top' style='" + pad + "'>"
             + "<table width='100%' cellpadding='0' cellspacing='0' border='0'>"
             + "<tr><td style='background-color:" + accent + ";border-radius:8px 8px 0 0;height:4px;font-size:0;line-height:0;'>&nbsp;</td></tr>"
             + "<tr><td style='background-color:" + accent + ";border-radius:0 0 8px 8px;padding:14px 10px;text-align:center;'>"
             + "<p style='margin:0 0 3px 0;font-family:Arial,sans-serif;font-size:22px;font-weight:bold;color:#ffffff;'>" + value + "</p>"
             + "<p style='margin:0;font-family:Arial,sans-serif;font-size:9px;color:#ffffff;text-transform:uppercase;letter-spacing:1px;opacity:0.85;'>" + icon + label + "</p>"
             + "</td></tr>"
             + "</table>"
             + "</td>";
    }
    /** Checklist row */
    private String checklistRow(String numColor, String num, String text) {
        return "<tr><td valign='top' style='padding:3px 0;'><p style='margin:0;font-family:Arial,sans-serif;font-size:13px;color:#374151;line-height:1.6;'>"
             + "<span style='color:" + numColor + ";font-weight:bold;'>" + num + "</span>&nbsp;" + text + "</p></td></tr>";
    }

    // ─────────────────────────────────────────────────────────────
    // SAFE VALUE HELPERS
    // ─────────────────────────────────────────────────────────────

    private String getSafeString(String value) {
        return value != null && !value.trim().isEmpty() ? value : "&mdash;";
    }

    private String getSafeBigDecimal(BigDecimal value) {
        return value != null ? value.toString() : "0";
    }

    private String getSafeInteger(Integer value) {
        return value != null ? value.toString() : "0";
    }

    private String getSafeEmployeeName(List<SubledgerAlertDTO> subledgers) {
        for (SubledgerAlertDTO s : subledgers) {
            if (s != null && s.getEmployee() != null && !s.getEmployee().trim().isEmpty())
                return s.getEmployee();
        }
        return "All";
    }

    // ─────────────────────────────────────────────────────────────
    // PUBLIC SEND METHODS
    // ─────────────────────────────────────────────────────────────

    public void sendSalespersonAlertEmailWithCC(String salespersonEmail, List<SubledgerAlertDTO> subledgers,
            List<String> ccEmails) throws MessagingException {
if (subledgers == null || subledgers.isEmpty()) {
log.warn("No subledgers provided for salesperson: {}", salespersonEmail);
return;
}

String employeeName = getSafeEmployeeName(subledgers);

long criticalCount = subledgers.stream().filter(s -> s != null && s.getPercentage() != null && s.getPercentage().doubleValue() >= 150).count();
long highCount     = subledgers.stream().filter(s -> s != null && s.getPercentage() != null && s.getPercentage().doubleValue() >= 120 && s.getPercentage().doubleValue() < 150).count();
long mediumCount   = subledgers.stream().filter(s -> s != null && s.getPercentage() != null && s.getPercentage().doubleValue() >= 100 && s.getPercentage().doubleValue() < 120).count();
long lowCount      = subledgers.stream().filter(s -> s != null && s.getPercentage() != null && s.getPercentage().doubleValue() >= 80  && s.getPercentage().doubleValue() < 100).count();

MimeMessage message = mailSender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

helper.setFrom("gjayabalan08@gmail.com");
helper.setTo(salespersonEmail);

if (ccEmails != null && !ccEmails.isEmpty()) {
helper.setCc(ccEmails.toArray(new String[0]));
log.info("Adding CC emails for {}: {}", salespersonEmail, String.join(", ", ccEmails));
}

if (StringUtils.hasText(bccAddress)) helper.setBcc(bccAddress);

helper.setSubject(String.format("Credit Limit Alert — %d Customers | %d Critical",
subledgers.size(), criticalCount));

helper.setText(buildSalespersonAlertEmailBody(employeeName, subledgers, salespersonEmail,
criticalCount, highCount, mediumCount, lowCount, ccEmails), true);

// ✅ ADD THIS LINE - Attach the company logo
attachCompanyLogo(helper);

mailSender.send(message);
log.info("Alert email sent to: {} ({} customers, {} critical)", salespersonEmail, subledgers.size(), criticalCount);
}
    public void sendSalespersonAlertEmail(String salespersonEmail, List<SubledgerAlertDTO> subledgers) throws MessagingException {
        sendSalespersonAlertEmailWithCC(salespersonEmail, subledgers, null);
    }
}