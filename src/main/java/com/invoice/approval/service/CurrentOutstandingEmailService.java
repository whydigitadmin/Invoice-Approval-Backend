package com.invoice.approval.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.invoice.approval.dto.CurrentOutstandingDTO;
import com.invoice.approval.repo.CurrentOutstandingRepo;

@Service
public class CurrentOutstandingEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentOutstandingEmailService.class);

    @Autowired private JavaMailSender mailSender;
    @Autowired private CurrentOutstandingRepo currentOutstandingRepo;
    @Autowired private EmailHistoryService emailHistoryService;  // NEW: Inject history service

    @Value("${email.bcc.address:}")                          private String bccAddress;
    @Value("${spring.mail.username:gjayabalan08@gmail.com}") private String fromEmail;
    @Value("${app.base.url:http://localhost:8091}")          private String baseUrl;

    // ==================== EXISTING METHODS (KEEP ALL YOUR EXISTING CODE) ====================
    
    private Map<String, List<CurrentOutstandingDTO>> groupOutstandingBySalesperson(List<CurrentOutstandingDTO> outstandingList) {
        Map<String, List<CurrentOutstandingDTO>> grouped = new HashMap<>();
        for (CurrentOutstandingDTO outstanding : outstandingList) {
            String salespersonEmail = outstanding.getSalespersonEmail();
            if (salespersonEmail == null || salespersonEmail.trim().isEmpty()) {
                LOGGER.debug("Skipping subledger {} - no salesperson email", outstanding.getSubledgerCode());
                continue;
            }
            salespersonEmail = salespersonEmail.trim().toLowerCase();
            if (!isValidEmail(salespersonEmail)) {
                LOGGER.warn("Skipping subledger {} - invalid email format: {}", outstanding.getSubledgerCode(), salespersonEmail);
                continue;
            }
            if (!grouped.containsKey(salespersonEmail)) grouped.put(salespersonEmail, new ArrayList<>());
            grouped.get(salespersonEmail).add(outstanding);
        }
        return grouped;
    }

    private List<String> getCCEmailsForRecipient(String recipientEmail, List<CurrentOutstandingDTO> outstandingList) {
        Set<String> ccEmails = new HashSet<>();
        for (CurrentOutstandingDTO outstanding : outstandingList) {
            if (outstanding.getCcMail() != null && !outstanding.getCcMail().trim().isEmpty()) {
                for (String cc : outstanding.getCcMail().split("[,\\s;]+")) {
                    String t = cc.trim().toLowerCase();
                    if (!t.equalsIgnoreCase(recipientEmail) && isValidEmail(t)) ccEmails.add(t);
                }
            }
        }
        return new ArrayList<>(ccEmails);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "&#8377;0.00";
        try { return String.format("&#8377;%,.2f", amount); }
        catch (Exception e) { return "&#8377;" + amount; }
    }

    private String formatDate(Date date) {
        if (date == null) return "&mdash;";
        return new SimpleDateFormat("dd MMM yyyy").format(date);
    }

    private String getSafeString(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "&mdash;";
    }

    private String getSalespersonName(List<CurrentOutstandingDTO> list) {
        for (CurrentOutstandingDTO item : list) {
            if (item.getSalespersonName() != null && !item.getSalespersonName().trim().isEmpty()) return item.getSalespersonName();
            if (item.getSalesperson()     != null && !item.getSalesperson().trim().isEmpty())     return item.getSalesperson();
        }
        return "Salesperson";
    }

    private static class CustomerSummary {
        String name, code, office;
        BigDecimal totalDue = BigDecimal.ZERO;
        int invoiceCount = 0, worstDays = 0;
        CustomerSummary(String name, String code, String office) { this.name=name; this.code=code; this.office=office; }
        void addInvoice(CurrentOutstandingDTO item) {
            invoiceCount++;
            if (item.getTotDue()     != null) totalDue = totalDue.add(item.getTotDue());
            if (item.getCreditDays() != null && item.getCreditDays() > worstDays) worstDays = item.getCreditDays();
        }
        String accentColor() { if (worstDays>90) return "#C62828"; if (worstDays>60) return "#E65100"; if (worstDays>30) return "#F57F17"; return "#2E7D32"; }
        String badgeBg()     { if (worstDays>90) return "#FFEBEE"; if (worstDays>60) return "#FBE9E7"; if (worstDays>30) return "#FFF8E1"; return "#E8F5E9"; }
        String statusLabel() { if (worstDays>90) return "Escalate now"; if (worstDays>60) return "Urgent"; if (worstDays>30) return "Follow up"; return "Within terms"; }
        boolean isCritical() { return worstDays > 90; }
    }

    private String buildDonut(List<CustomerSummary> customers) {
        String[] PALETTE = {
            "#1976D2", "#2E7D32", "#F57F17", "#C62828",
            "#6A1B9A", "#00838F", "#AD1457", "#E65100"
        };
        String OTHERS_COLOR = "#9E9E9E";

        BigDecimal grandTotal = customers.stream()
                .map(c -> c.totalDue).filter(d -> d.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (grandTotal.compareTo(BigDecimal.ZERO) == 0) grandTotal = BigDecimal.ONE;

        List<String> segNames = new ArrayList<>();
        List<BigDecimal> segAmts = new ArrayList<>();
        List<String> segColors = new ArrayList<>();

        BigDecimal othersTotal = BigDecimal.ZERO;
        int colorIdx = 0;
        for (CustomerSummary c : customers) {
            if (c.totalDue.compareTo(BigDecimal.ZERO) <= 0) continue;
            if (colorIdx < PALETTE.length) {
                segNames.add(c.name);
                segAmts.add(c.totalDue);
                segColors.add(PALETTE[colorIdx++]);
            } else {
                othersTotal = othersTotal.add(c.totalDue);
            }
        }
        if (othersTotal.compareTo(BigDecimal.ZERO) > 0) {
            segNames.add("Others");
            segAmts.add(othersTotal);
            segColors.add(OTHERS_COLOR);
        }

        int N = segNames.size();
        if (N == 0) return "";

        double[] proportions = new double[N];
        for (int i = 0; i < N; i++)
            proportions[i] = segAmts.get(i).divide(grandTotal, 10, java.math.RoundingMode.HALF_UP).doubleValue();

        double C = 2 * Math.PI * 60;

        StringBuilder sb = new StringBuilder();

        sb.append("<!--[if mso]>")
          .append("<div style='width:140px;height:140px;position:relative;'>");

        int cx = 80, cy_vml = 80, ro = 72, ri = 42;
        double startDeg = -90.0;

        for (int i = 0; i < N; i++) {
            double sweep = proportions[i] * 360.0;
            double endDeg = startDeg + sweep;

            double sRad = Math.toRadians(startDeg);
            double eRad = Math.toRadians(endDeg);

            int ox1 = (int) Math.round(cx + ro * Math.cos(sRad));
            int oy1 = (int) Math.round(cy_vml + ro * Math.sin(sRad));
            int ix2 = (int) Math.round(cx + ri * Math.cos(eRad));
            int iy2 = (int) Math.round(cy_vml + ri * Math.sin(eRad));

            long vmlStart = Math.round(-startDeg * 65536);
            long vmlSweep = Math.round(-sweep * 65536);
            long vmlStartRev = Math.round(-endDeg * 65536);
            long vmlSweepRev = Math.round(sweep * 65536);

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

        sb.append("<v:oval xmlns:v='urn:schemas-microsoft-com:vml'")
          .append(" style='position:absolute;top:").append(cy_vml - ri).append("px;")
          .append("left:").append(cx - ri).append("px;")
          .append("width:").append(ri * 2).append("px;height:").append(ri * 2).append("px;'")
          .append(" fillcolor='#ffffff' stroked='false'></v:oval>");

        sb.append("</div>")
          .append("<![endif]-->");

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

        sb.append("<circle cx='80' cy='80' r='44' fill='#ffffff'/>")
          .append("</svg>")
          .append("<!--<![endif]-->");

        return sb.toString();
    }
    
    private String extractNameFromEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "User";
        }
        
        // Get everything before @
        String namePart = email.split("@")[0];
        
        // Get only the part before the first dot
        if (namePart.contains(".")) {
            namePart = namePart.split("\\.")[0];
        }
        
        // Replace underscores with spaces and capitalize
        namePart = namePart.replace("_", " ");
        namePart = Arrays.stream(namePart.split(" "))
                       .filter(word -> !word.isEmpty())
                       .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                       .collect(Collectors.joining(" "));
        
        return namePart.isEmpty() ? "User" : namePart;
    }

    private String buildOutstandingEmailBody(String salespersonName,
                                             List<CurrentOutstandingDTO> outstandingList,
                                             String salespersonEmail,
                                             List<String> ccEmails) {

        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        String safeSalespersonName = getSafeString(salespersonName);
        String firstName = safeSalespersonName.contains(" ")
                ? safeSalespersonName.substring(0, safeSalespersonName.indexOf(' '))
                : safeSalespersonName;
        String initials = Arrays.stream(safeSalespersonName.split("\\s+"))
                .filter(w -> !w.isEmpty()).limit(2)
                .map(w -> String.valueOf(w.charAt(0)).toUpperCase())
                .collect(Collectors.joining());
        if (initials.isEmpty()) initials = "SP";

        BigDecimal totalOutstanding = outstandingList.stream().map(CurrentOutstandingDTO::getTotDue)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        long uniqueCustomers = outstandingList.stream().map(CurrentOutstandingDTO::getSubledgerCode).distinct().count();
        long invoiceCount = outstandingList.size();

        long aging0_30 = outstandingList.stream().filter(d -> d.getCreditDays() != null && d.getCreditDays() <= 30).count();
        long aging31_60 = outstandingList.stream().filter(d -> d.getCreditDays() != null && d.getCreditDays() > 30 && d.getCreditDays() <= 60).count();
        long aging61_90 = outstandingList.stream().filter(d -> d.getCreditDays() != null && d.getCreditDays() > 60 && d.getCreditDays() <= 90).count();
        long aging90Plus = outstandingList.stream().filter(d -> d.getCreditDays() != null && d.getCreditDays() > 90).count();
        long overdueCount = aging61_90 + aging90Plus;
        long total4Chart = Math.max(1, aging0_30 + aging31_60 + aging61_90 + aging90Plus);

        String displayName = extractNameFromEmail(salespersonEmail);

        Map<String, CustomerSummary> customerMap = new LinkedHashMap<>();
        for (CurrentOutstandingDTO item : outstandingList) {
            String code = item.getSubledgerCode();
            if (code == null) continue;
            customerMap.computeIfAbsent(code, k -> new CustomerSummary(
                    getSafeString(item.getSubledgerName()), code, getSafeString(item.getCtrlOffice())
            )).addInvoice(item);
        }
        List<CustomerSummary> customers = new ArrayList<>(customerMap.values());

        StringBuilder h = new StringBuilder();

        h.append("<!DOCTYPE html>")
                .append("<html lang='en' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:o='urn:schemas-microsoft-com:office:office'>")
                .append("<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'>")
                .append("<meta http-equiv='X-UA-Compatible' content='IE=edge'>")
                .append("<!--[if mso]><noscript><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml></noscript><![endif]-->")
                .append("<style>")
                .append("body,table,td,p,a,span{font-family:Arial,sans-serif;}")
                .append("body{margin:0;padding:0;background-color:#F0F2F5;}a{color:#1565C0;}")
                .append("v\\:*{behavior:url(#default#VML);display:inline-block;}")
                .append("</style>")
                .append("</head>")
                .append("<body style='margin:0;padding:0;background-color:#F0F2F5;'>")
                .append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='background-color:#F0F2F5;'>")
                .append("<tr><td align='center' style='padding:24px 12px;'>")
                .append("<table width='860' cellpadding='0' cellspacing='0' border='0' style='max-width:860px;background-color:#ffffff;border-collapse:collapse;mso-table-lspace:0;mso-table-rspace:0;'>");

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
                .append("<td valign='middle' align='right'>")
                .append("<table cellpadding='0' cellspacing='0' border='0'><tr>")
                .append("<td style='background-color:#1a3f6f;border:1px solid #2a5080;padding:7px 14px;'>")
                .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:11px;color:#B8CDE0;white-space:nowrap;'>&#128197;&nbsp;").append(currentDateTime).append("</p>")
                .append("</td></tr></table>")
                .append("</td>")
                .append("</tr></table>")
                .append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-top:15px;'><tr>")
                .append("<td align='center' style='text-align:center;'>")
                .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:22px;font-weight:bold;color:#ffffff;'>Customer & Billwise Outstanding</p>")
                .append("</td>")
                .append("</tr></table>")
                .append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-top:20px;'>")
                .append("<tr><td style='border-top:1px solid #1e3a5f;padding-top:18px;'>")
                .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:18px;font-weight:bold;color:#ffffff;'>Hi Mr./Ms. ").append(displayName).append("</p>")
                .append("</td></tr></table>")
                .append("</td></tr>");

        h.append("<tr><td style='background-color:#EBF3FD;padding:20px 40px;border-bottom:1px solid #D0E4F7;'>")
                .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:14px;color:#1a3a5c;line-height:1.7;'>")
                .append("Here is your outstanding collection report as of today. ")
                .append("The table below lists all pending invoices across your customer accounts. ")
                .append("<strong>Please follow up with customers on overdue items</strong> &mdash; especially those beyond 60 days &mdash; ")
                .append("and update the collections team on expected payment dates.")
                .append("</p></td></tr>");

        h.append("<tr><td style='padding:28px 40px;background-color:#ffffff;'>")
                .append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:8px;'><tr>")
                .append(kpiCell(formatCurrency(totalOutstanding), "Your Total Outstanding", "#1976D2", "33%", "padding-right:8px;"))
                .append(kpiCell(String.valueOf(uniqueCustomers), "Customers to Follow Up", "#2E7D32", "33%", "padding-left:4px;padding-right:4px;"))
                .append(kpiCellRed(String.valueOf(overdueCount), "Overdue (&gt;60 Days)", "33%", "padding-left:8px;"))
                .append("</tr></table>")
                .append("<p style='margin:0 0 28px 0;font-family:Arial,sans-serif;font-size:11px;color:#9aa3b2;'>&#9432;&nbsp; Overdue count includes invoices beyond 60 days requiring immediate attention.</p>");

        List<CustomerSummary> sortedCustomers = customers.stream()
                .filter(c -> c.totalDue.compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.totalDue.compareTo(a.totalDue))
                .collect(Collectors.toList());

        String[] DONUT_PALETTE = {
                "#1976D2", "#2E7D32", "#F57F17", "#C62828",
                "#6A1B9A", "#00838F", "#AD1457", "#E65100"
        };
        String OTHERS_COLOR = "#9E9E9E";

        h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:40px;background-color:#FAFBFF;border:1px solid #E3E9FF;'>")
                .append("<tr><td style='padding:20px 24px;'>")
                .append("<p style='margin:0 0 18px 0;font-family:Arial,sans-serif;font-size:10px;color:#9aa3b8;text-transform:uppercase;letter-spacing:1.5px;'>Customer-wise Outstanding</p>")
                .append("<table width='100%' cellpadding='0' cellspacing='0' border='0'><tr>")
                .append("<td width='180' valign='middle' align='center' style='padding-right:24px;'>")
                .append(buildDonut(sortedCustomers))
                .append("</td>")
                .append("<td valign='middle'><table cellpadding='0' cellspacing='0' border='0' width='100%'>");

        BigDecimal gtForPct = sortedCustomers.stream().map(c -> c.totalDue).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (gtForPct.compareTo(BigDecimal.ZERO) == 0) gtForPct = BigDecimal.ONE;

        BigDecimal othersAmt = BigDecimal.ZERO;
        int ci = 0;
        boolean hasOthers = sortedCustomers.size() > DONUT_PALETTE.length;
        for (CustomerSummary cs : sortedCustomers) {
            if (ci < DONUT_PALETTE.length) {
                int pct = cs.totalDue.multiply(new BigDecimal("100"))
                        .divide(gtForPct, 0, java.math.RoundingMode.HALF_UP).intValue();
                int bar = cs.totalDue.multiply(new BigDecimal("100"))
                        .divide(sortedCustomers.get(0).totalDue, 0, java.math.RoundingMode.HALF_UP).intValue();
                String displayName1 = cs.name.length() > 22 ? cs.name.substring(0, 20) + "&hellip;" : cs.name;
                boolean isLast = (ci == sortedCustomers.size() - 1) && !hasOthers;
                h.append(legendRowCustomer(DONUT_PALETTE[ci], displayName1, cs.code, formatCurrency(cs.totalDue), bar, pct, !isLast));
            } else {
                othersAmt = othersAmt.add(cs.totalDue);
            }
            ci++;
        }
        if (othersAmt.compareTo(BigDecimal.ZERO) > 0) {
            int pct = othersAmt.multiply(new BigDecimal("100"))
                    .divide(gtForPct, 0, java.math.RoundingMode.HALF_UP).intValue();
            int bar = sortedCustomers.get(0).totalDue.compareTo(BigDecimal.ZERO) > 0
                    ? othersAmt.multiply(new BigDecimal("100"))
                    .divide(sortedCustomers.get(0).totalDue, 0, java.math.RoundingMode.HALF_UP).intValue()
                    : 0;
            h.append(legendRowCustomer(OTHERS_COLOR, "Others", "", formatCurrency(othersAmt), bar, pct, false));
        }

        h.append("</table></td></tr></table></td></tr></table>");

        if (aging90Plus > 0) {
            h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:28px;'><tr>")
                    .append("<td width='4' style='background-color:#C62828;font-size:0;line-height:0;'>&nbsp;</td>")
                    .append("<td style='background-color:#FFF3F3;padding:14px 18px;border:1px solid #FFCDD2;border-left:none;'>")
                    .append("<p style='margin:0 0 4px 0;font-family:Arial,sans-serif;font-size:13px;font-weight:bold;color:#B71C1C;'>&#9888;&nbsp; Action Required</p>")
                    .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:13px;color:#7f1d1d;line-height:1.6;'>")
                    .append("You have <strong>").append(aging90Plus).append(" invoice").append(aging90Plus > 1 ? "s" : "").append(" beyond 90 days</strong>. ")
                    .append("Contact these customers today and confirm payment dates. Unresolved accounts may be escalated for credit hold.")
                    .append("</p></td></tr></table>");
        }

        if (ccEmails != null && !ccEmails.isEmpty()) {
            h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:28px;'><tr>")
                    .append("<td width='4' style='background-color:#1976D2;font-size:0;line-height:0;'>&nbsp;</td>")
                    .append("<td style='background-color:#EEF6FF;padding:11px 16px;border:1px solid #BBDEFB;border-left:none;'>")
                    .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:12px;color:#0d47a1;'>&#128231;&nbsp;<strong>CC:</strong>&nbsp;")
                    .append(String.join(" &bull; ", ccEmails))
                    .append(" &mdash; Your manager and the finance team have been copied on this report.")
                    .append("</p></td></tr></table>");
        }

        h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0'>")
                .append("<tr><td style='height:40px; font-size:0; line-height:0;'>&nbsp;</td></tr>")
                .append("</table>");

        String encodedEmail = "", encodedName = "";
        try {
            encodedEmail = URLEncoder.encode(salespersonEmail != null ? salespersonEmail : "", "UTF-8");
            encodedName = URLEncoder.encode(displayName, "UTF-8");
        } catch (Exception e) {
            encodedEmail = (salespersonEmail != null ? salespersonEmail : "").replace(" ", "%20");
            encodedName = displayName.replace(" ", "%20");
        }
        String downloadLink = baseUrl + "/api/outstanding/download-outstanding?salespersonEmail="
                + encodedEmail + "&employeeName=" + encodedName;

        h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:24px;'><tr>")
                .append("<td align='center' style='padding:4px 0;'>")
                .append("<table cellpadding='0' cellspacing='0' border='0'><tr>")
                .append("<td style='background-color:#2E7D32;padding:12px 28px;'>")
                .append("<a href='").append(downloadLink).append("' style='font-family:Arial,sans-serif;font-size:13px;font-weight:bold;color:#ffffff;text-decoration:none;'>")
                .append("&#128202;&nbsp; Download Pending Invoices (Excel)")
                .append("</a></td></tr></table>")
                .append("<p style='margin:6px 0 0;font-family:Arial,sans-serif;font-size:11px;color:#9aa3b2;'>Download your complete pending invoices data in Excel format</p>")
                .append("</td></tr></table>");

        h.append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-top:50px;margin-bottom:10px;'><tr><td>")
                .append("<p style='margin:0 0 4px 0;font-family:Arial,sans-serif;font-size:14px;font-weight:bold;color:#0f2744;'>")
                .append("Pending Invoices &mdash; Your Accounts")
                .append("&nbsp;<span style='background-color:#E3EEFF;color:#1565C0;font-family:Arial,sans-serif;font-size:11px;font-weight:bold;padding:3px 9px;'>").append(invoiceCount).append(" invoices</span></p>")
                .append("<p style='margin:4px 0 12px 0;font-family:Arial,sans-serif;font-size:12px;color:#7a8aaa;'>Review each outstanding invoice and initiate collection follow-up where required.</p>")
                .append("</td></tr></table>")
                .append("<table width='100%' cellpadding='0' cellspacing='0' border='1' bordercolor='#E4E8F4' style='border-collapse:collapse;font-family:Arial,sans-serif;font-size:12px;'>")
                .append("<thead><tr style='background-color:#0f2744;'>")
                .append(th("center", "#")).append(th("left", "CUSTOMER CODE")).append(th("left", "CUSTOMER NAME"))
                .append(th("left", "INVOICE NO")).append(th("left", "INVOICE DATE")).append(th("left", "REF NO"))
                .append(th("left", "REF DATE")).append(th("center", "AGE")).append(th("right", "CREDIT LIMIT"))
                .append(th("left", "BRANCH")).append(th("right", "AMOUNT DUE"))
                .append("</tr></thead><tbody>");

        int sno = 1;
        for (CurrentOutstandingDTO item : outstandingList) {
            if (item == null) continue;
            boolean isCritical = item.getCreditDays() != null && item.getCreditDays() > 90;
            String rowBg = isCritical ? "#FFF8F8" : (sno % 2 == 0 ? "#F7F9FF" : "#ffffff");
            String cb = isCritical ? "1px solid #FFCDD2" : "1px solid #E8ECF4";
            String cdBg, cdColor, cdLabel;
            if (item.getCreditDays() == null) {
                cdBg = "#F0F2F5";
                cdColor = "#9aa3b8";
                cdLabel = "&mdash;";
            } else if (item.getCreditDays() > 90) {
                cdBg = "#FFEBEE";
                cdColor = "#C62828";
                cdLabel = item.getCreditDays() + "d &#9888;";
            } else if (item.getCreditDays() > 60) {
                cdBg = "#FBE9E7";
                cdColor = "#E65100";
                cdLabel = item.getCreditDays() + "d";
            } else if (item.getCreditDays() > 30) {
                cdBg = "#FFF8E1";
                cdColor = "#F57F17";
                cdLabel = item.getCreditDays() + "d";
            } else {
                cdBg = "#E8F5E9";
                cdColor = "#2E7D32";
                cdLabel = item.getCreditDays() + "d";
            }
            String ns = isCritical ? "font-family:Arial,sans-serif;font-weight:bold;color:#B71C1C;" : "font-family:Arial,sans-serif;font-weight:bold;color:#1a1a2e;";
            String ac = (item.getTotDue() != null && item.getTotDue().compareTo(BigDecimal.ZERO) > 0) ? "#1B5E20" : "#B71C1C";

            h.append("<tr style='background-color:").append(rowBg).append(";'>")
                    .append("<td style='padding:10px 8px;text-align:center;border:").append(cb).append(";color:#3730a3;font-weight:bold;font-size:11px;'>").append(sno).append("</td>")
                    .append("<td style='padding:10px 8px;border:").append(cb).append(";'><span style='background-color:#F1F5FF;color:#1e40af;font-family:Courier New,monospace;font-size:11px;font-weight:bold;padding:2px 6px;'>").append(getSafeString(item.getSubledgerCode())).append("</span></td>")
                    .append("<td style='padding:10px 8px;border:").append(cb).append(";'><span style='").append(ns).append("'>").append(getSafeString(item.getSubledgerName())).append("</span></td>")
                    .append("<td style='padding:10px 8px;border:").append(cb).append(";color:#374151;'>").append(getSafeString(item.getDocId())).append("</td>")
                    .append("<td style='padding:10px 8px;border:").append(cb).append(";color:#374151;white-space:nowrap;'>").append(formatDate(item.getDocDate())).append("</td>")
                    .append("<td style='padding:10px 8px;border:").append(cb).append(";color:#374151;'>").append(getSafeString(item.getRefNo())).append("</td>")
                    .append("<td style='padding:10px 8px;border:").append(cb).append(";color:#374151;white-space:nowrap;'>").append(formatDate(item.getRefDate())).append("</td>")
                    .append("<td style='padding:10px 8px;text-align:center;border:").append(cb).append(";'><span style='background-color:").append(cdBg).append(";color:").append(cdColor).append(";font-size:11px;font-weight:bold;padding:3px 8px;'>").append(cdLabel).append("</span></td>")
                    .append("<td style='padding:10px 8px;text-align:right;border:").append(cb).append(";color:#374151;'>").append(formatCurrency(item.getCreditLimit())).append("</td>")
                    .append("<td style='padding:10px 8px;border:").append(cb).append(";color:#374151;'>").append(getSafeString(item.getCtrlOffice())).append("</td>")
                    .append("<td style='padding:10px 8px;text-align:right;border:").append(cb).append(";color:").append(ac).append(";font-weight:bold;'>").append(formatCurrency(item.getTotDue())).append("</td>")
                    .append("</tr>");
            sno++;
        }

        h.append("</tbody></table>")
                .append("</td></tr>")

                .append("<tr><td style='background-color:#0f2744;padding:24px 40px;'>")
                .append("<table width='100%' cellpadding='0' cellspacing='0' border='0'><tr>")
                .append("<td valign='top' width='60%'>")
                .append("<p style='margin:0 0 3px 0;font-family:Arial,sans-serif;font-size:13px;font-weight:bold;color:rgba(255,255,255,0.65);'>Credit Monitoring System</p>")
                .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:12px;color:rgba(255,255,255,0.35);line-height:1.7;'>")
                .append("This report is generated automatically and sent only to you.<br>Please do not forward this email outside your team.")
                .append("</p></td>")
                .append("<td valign='top' align='right' width='40%'>")
                .append("<p style='margin:0 0 3px 0;font-family:Arial,sans-serif;font-size:11px;color:rgba(255,255,255,0.35);'>Queries?</p>")
                .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:12px;'><a href='mailto:it.tech@uniworld-logistics.com' style='color:#64B5F6;text-decoration:none;'>it.tech@uniworld-logistics.com</a></p>")
                .append("</td></tr></table>")
                .append("<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin:16px 0 12px 0;'><tr><td style='border-top:1px solid #1e3a5f;font-size:0;line-height:0;'>&nbsp;</td></tr></table>")
                .append("<p style='margin:0;font-family:Arial,sans-serif;font-size:11px;color:rgba(255,255,255,0.25);text-align:center;'>&copy; ").append(LocalDateTime.now().getYear()).append(" Uniworld Logistics Pvt Ltd. All rights reserved.</p>")
                .append("</td></tr>")
                .append("</table></td></tr></table></body></html>");

        return h.toString();
    }

    private String th(String align, String label) {
        return "<th style='padding:11px 8px;color:#B8CDE0;font-size:10px;font-weight:bold;text-align:" + align + ";white-space:nowrap;border:1px solid #1a3f6f;'>" + label + "</th>";
    }

    private String kpiCell(String value, String label, String accent, String width, String pad) {
        return "<td width='" + width + "' valign='top' style='" + pad + "'><table width='100%' cellpadding='0' cellspacing='0' border='0'>"
                + "<tr><td style='background-color:" + accent + ";height:3px;font-size:0;line-height:0;'>&nbsp;</td></tr>"
                + "<tr><td style='background-color:#F7F9FF;border:1px solid #E0E8FF;border-top:none;padding:16px;text-align:center;'>"
                + "<p style='margin:0 0 3px 0;font-family:Arial,sans-serif;font-size:17px;font-weight:bold;color:#0f2744;'>" + value + "</p>"
                + "<p style='margin:0;font-family:Arial,sans-serif;font-size:9px;color:#7a8aaa;text-transform:uppercase;letter-spacing:1px;'>" + label + "</p>"
                + "</td></tr></table></td>";
    }

    private String kpiCellRed(String value, String label, String width, String pad) {
        return "<td width='" + width + "' valign='top' style='" + pad + "'><table width='100%' cellpadding='0' cellspacing='0' border='0'>"
                + "<tr><td style='background-color:#C62828;height:3px;font-size:0;line-height:0;'>&nbsp;</td></tr>"
                + "<tr><td style='background-color:#FFF8F8;border:1px solid #FFE0E0;border-top:none;padding:16px;text-align:center;'>"
                + "<p style='margin:0 0 3px 0;font-family:Arial,sans-serif;font-size:26px;font-weight:bold;color:#C62828;'>" + value + "</p>"
                + "<p style='margin:0;font-family:Arial,sans-serif;font-size:9px;color:#C62828;text-transform:uppercase;letter-spacing:1px;'>" + label + "</p>"
                + "</td></tr></table></td>";
    }

    private String legendRowCustomer(String color, String name, String code, String amount, int barPct, int pct, boolean divider) {
        String codeStr = (code != null && !code.isEmpty())
                ? "<span style='font-family:Courier New,monospace;font-size:10px;color:#9aa3b8;margin-left:4px;'>" + code + "</span>"
                : "";
        return "<tr><td style='padding:5px 0;'>"
                + "<table cellpadding='0' cellspacing='0' border='0' width='100%'><tr>"
                + "<td width='12' valign='middle' style='padding-right:10px;'><div style='width:12px;height:12px;background-color:" + color + ";'>&nbsp;</div></td>"
                + "<td valign='middle'>"
                + "<p style='margin:0;font-family:Arial,sans-serif;font-size:12px;color:#374151;font-weight:bold;'>" + name + codeStr + "</p>"
                + "<p style='margin:1px 0 0;font-family:Arial,sans-serif;font-size:11px;color:" + color + ";font-weight:bold;'>" + amount + "</p>"
                + "</td>"
                + "<td align='right' valign='middle' style='padding-left:10px;'>"
                + "<table cellpadding='0' cellspacing='0' border='0' style='width:100px;'><tr>"
                + "<td style='background-color:#E8ECF4;height:6px;font-size:0;line-height:0;'>"
                + "<table width='" + barPct + "%' cellpadding='0' cellspacing='0' border='0'><tr><td style='background-color:" + color + ";height:6px;font-size:0;line-height:0;'>&nbsp;</td></tr></table>"
                + "</td></tr></table></td>"
                + "<td align='right' valign='middle' width='36' style='padding-left:6px;'>"
                + "<p style='margin:0;font-family:Arial,sans-serif;font-size:11px;font-weight:bold;color:" + color + ";'>" + pct + "%</p></td>"
                + "</tr></table>"
                + "</td></tr>"
                + (divider ? "<tr><td style='border-top:1px solid #EEF1F8;font-size:0;line-height:0;'>&nbsp;</td></tr>" : "");
    }

    // ==================== UPDATED SEND METHOD WITH HISTORY ====================

    public void sendOutstandingEmailToSalesperson(String salespersonEmail,
                                                  List<CurrentOutstandingDTO> outstandingList,
                                                  List<String> ccEmails) throws MessagingException {
        if (outstandingList == null || outstandingList.isEmpty()) {
            LOGGER.warn("No outstanding data provided for salesperson: {}", salespersonEmail);
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Extract name from email (everything before @)
        String salespersonName = salespersonEmail;
        if (salespersonEmail != null && salespersonEmail.contains("@")) {
            salespersonName = salespersonEmail.substring(0, salespersonEmail.indexOf('@'));
            salespersonName = salespersonName.replace(".", " ").replace("_", " ");
            salespersonName = Arrays.stream(salespersonName.split(" "))
                    .filter(word -> !word.isEmpty())
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));
        }

        BigDecimal totalOutstanding = outstandingList.stream().map(CurrentOutstandingDTO::getTotDue)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        int customerCount = (int) outstandingList.stream().map(CurrentOutstandingDTO::getSubledgerCode).distinct().count();

        helper.setFrom(fromEmail);
        helper.setTo(salespersonEmail);

        if (ccEmails != null && !ccEmails.isEmpty()) {
            helper.setCc(ccEmails.toArray(new String[0]));
            LOGGER.info("Adding CC emails for {}: {}", salespersonEmail, String.join(", ", ccEmails));
        }
        if (StringUtils.hasText(bccAddress)) helper.setBcc(bccAddress);

        String subject = String.format("Outstanding Statement \u2014 %d Customers | Total: %s",
                customerCount, formatCurrency(totalOutstanding).replace("&#8377;", "\u20B9"));
        helper.setSubject(subject);

        // Set the HTML content
        helper.setText(buildOutstandingEmailBody(salespersonName, outstandingList, salespersonEmail, ccEmails), true);

        // ATTACH THE LOGO AS INLINE CID
        try {
            ClassPathResource logo = new ClassPathResource("static/logoonly.png");
            helper.addInline("companyLogo", logo);
            LOGGER.debug("Logo attached successfully for email to: {}", salespersonEmail);
        } catch (Exception logoEx) {
            LOGGER.warn("Could not attach logo for {}: {}", salespersonEmail, logoEx.getMessage());
        }

        try {
            mailSender.send(message);
            LOGGER.info("Email sent to: {} ({} documents, {} customers)", salespersonEmail, outstandingList.size(), customerCount);

            // RECORD SUCCESS IN HISTORY
            emailHistoryService.recordSuccess(
                    salespersonEmail,
                    salespersonName,
                    salespersonEmail,
                    ccEmails,
                    "DAILY_OUTSTANDING",
                    customerCount,
                    outstandingList.size(),
                    totalOutstanding
            );

        } catch (Exception e) {
            LOGGER.error("Failed to send email to {}: {}", salespersonEmail, e.getMessage());

            // RECORD FAILURE IN HISTORY
            emailHistoryService.recordFailure(
                    salespersonEmail,
                    salespersonName,
                    salespersonEmail,
                    ccEmails,
                    "DAILY_OUTSTANDING",
                    e.getMessage()
            );

            throw e; // Re-throw the exception
        }
    }

    public void processAndSendOutstandingEmails() {
        LOGGER.info("Starting current outstanding email process...");
        List<CurrentOutstandingDTO> allOutstanding = currentOutstandingRepo.getCurrentOutstandingWithEmailDetails();
        LOGGER.info("Retrieved {} outstanding records", allOutstanding.size());
        if (allOutstanding.isEmpty()) {
            LOGGER.info("No outstanding records found");
            return;
        }
        Map<String, List<CurrentOutstandingDTO>> grouped = groupOutstandingBySalesperson(allOutstanding);
        LOGGER.info("Grouped into {} salespersons", grouped.size());
        int ok = 0, fail = 0;
        for (Map.Entry<String, List<CurrentOutstandingDTO>> entry : grouped.entrySet()) {
            try {
                sendOutstandingEmailToSalesperson(entry.getKey(), entry.getValue(),
                        getCCEmailsForRecipient(entry.getKey(), entry.getValue()));
                ok++;
            } catch (Exception e) {
                LOGGER.error("Failed to send to {}: {}", entry.getKey(), e.getMessage(), e);
                fail++;
            }
        }
        LOGGER.info("Process completed. Success: {}, Failed: {}", ok, fail);
    }

    public void sendOutstandingForSalesperson(String salespersonEmail) throws MessagingException {
        List<CurrentOutstandingDTO> list = currentOutstandingRepo.getOutstandingBySalesperson(salespersonEmail);
        if (list.isEmpty()) throw new RuntimeException("No outstanding data found for salesperson: " + salespersonEmail);
        sendOutstandingEmailToSalesperson(salespersonEmail, list, getCCEmailsForRecipient(salespersonEmail, list));
    }

//    @Scheduled(cron = "0 45 12 * * ?")
    public void scheduledOutstandingEmailJob() {
        LOGGER.info("Running scheduled outstanding email job");
        processAndSendOutstandingEmails();
    }

//    @Scheduled(cron = "0 0 9 ? * MON")
    public void weeklyOutstandingEmailJob() {
        LOGGER.info("Running weekly outstanding email job");
        processAndSendOutstandingEmails();
    }
}