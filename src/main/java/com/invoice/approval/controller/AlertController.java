package com.invoice.approval.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.dto.SubledgerAlertDTO;
import com.invoice.approval.repo.SubledgerRepo;
import com.invoice.approval.service.SubledgerAlertService;

@RestController
@RequestMapping("/api/alerts")
public class AlertController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertController.class);

    @Autowired
    private SubledgerAlertService subledgerAlertService;

    @Autowired
    private SubledgerRepo subledgerRepo;

    @PostMapping("/subledger-check")
    public ResponseEntity<ResponseDTO> checkAndSendSubledgerAlerts() {
        String methodName = "checkAndSendSubledgerAlerts()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO;
        
        try {
            subledgerAlertService.checkAndSendSubledgerAlerts();
            
            responseObjectsMap.put("message", "Subledger alert check completed successfully");
            responseObjectsMap.put("timestamp", LocalDateTime.now());
            
            responseDTO = createServiceResponse(responseObjectsMap);
            
        } catch (Exception e) {
            LOGGER.error("Error in {}: {}", methodName, e.getMessage());
            responseDTO = createServiceResponseError(
                responseObjectsMap, 
                "Failed to check and send subledger alerts",
                e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        
        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/subledger-status")
    public ResponseEntity<ResponseDTO> getSubledgerAlertStatus() {
        String methodName = "getSubledgerAlertStatus()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO;
        
        try {
            responseObjectsMap.put("message", "Subledger alert system is active");
            responseObjectsMap.put("recipientEmail", "jayabalan.guru@uniworld-logistics.com");
            responseObjectsMap.put("checkCriteria", "Percentage between 80-100%, excluding Corporate category");
            
            responseDTO = createServiceResponse(responseObjectsMap);
            
        } catch (Exception e) {
            LOGGER.error("Error in {}: {}", methodName, e.getMessage());
            responseDTO = createServiceResponseError(
                responseObjectsMap, 
                "Failed to get subledger alert status",
                e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        
        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/subledger-check")
    public ResponseEntity<ResponseDTO> checkAndSendSubledgerAlertsGet() {
        return checkAndSendSubledgerAlerts();
    }

    @GetMapping("/download-excel")
    public ResponseEntity<?> downloadCreditAlertExcel(
            @RequestParam String salespersonEmail,
            @RequestParam(required = false) String employeeName) {
        
        String methodName = "downloadCreditAlertExcel()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        
        try {
            List<SubledgerAlertDTO> alertSubledgers;
            
            if (salespersonEmail != null && !salespersonEmail.trim().isEmpty()) {
                // Get ALL data first
                List<SubledgerAlertDTO> allSubledgers = subledgerRepo.getSubledgersWithHighPercentage();
                
                // Filter for this salesperson with the SAME conditions as email
                alertSubledgers = allSubledgers.stream()
                        .filter(s -> s != null)
                        .filter(s -> s.getMailid() != null && salespersonEmail.equalsIgnoreCase(s.getMailid().trim()))
                        .filter(s -> s.getPercentage() != null && s.getPercentage().compareTo(new BigDecimal("80")) >= 0)
                        // Add category filter if applicable
//                        .filter(s -> s.getCategory() == null || !"CORPORATE".equalsIgnoreCase(s.getCategory().trim()))
                        .collect(Collectors.toList());
                
                LOGGER.info("Downloading Excel for salesperson: {} - Found {} records", 
                           salespersonEmail, alertSubledgers.size());
                
            } else {
                // Download all data
                alertSubledgers = subledgerRepo.getSubledgersWithHighPercentage();
                LOGGER.info("Downloading Excel for all data with {} records", 
                           alertSubledgers != null ? alertSubledgers.size() : 0);
            }
            
            if (alertSubledgers == null || alertSubledgers.isEmpty()) {
                return createErrorResponse("No data available for Excel download");
            }
            
            // Apply final filtering
            List<SubledgerAlertDTO> filteredSubledgers = alertSubledgers.stream()
                    .filter(s -> s != null)
                    .filter(s -> s.getPercentage() != null && s.getPercentage().compareTo(new BigDecimal("80")) >= 0)
                    .collect(Collectors.toList());
            
            LOGGER.info("Final filtered count for Excel: {} records", filteredSubledgers.size());
            
            // Calculate priority distribution for logging
            Map<String, Long> priorityCounts = filteredSubledgers.stream()
                    .collect(Collectors.groupingBy(
                        s -> getPriorityLabel(s.getPercentage()),
                        Collectors.counting()
                    ));
            
            LOGGER.info("Priority distribution in Excel: {}", priorityCounts);
            
            Workbook workbook = createExcelWorkbook(filteredSubledgers, employeeName);
            ByteArrayResource resource = convertWorkbookToResource(workbook);
            
            String filename = generateFilename(employeeName);
            
            LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
                    
        } catch (Exception e) {
            LOGGER.error("Error in {}: {}", methodName, e.getMessage(), e);
            return createErrorResponse("Failed to generate Excel file: " + e.getMessage());
        }
    }

    private String getPriorityLabel(BigDecimal percentage) {
        if (percentage == null) return "UNKNOWN";
        int pct = percentage.intValue();
        
        if (pct >= 150) return "CRITICAL";
        else if (pct >= 120) return "HIGH";
        else if (pct >= 100) return "MEDIUM";
        else return "LOW";
    }

    private String generateFilename(String employeeName) {
        String baseName = "Credit_Alert_Report";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        if (employeeName != null && !employeeName.trim().isEmpty()) {
            // Clean the employee name for filename
            String cleanName = employeeName.replaceAll("[^a-zA-Z0-9]", "_");
            return baseName + "_" + cleanName + "_" + timestamp + ".xlsx";
        } else {
            return baseName + "_" + timestamp + ".xlsx";
        }
    }

    private Workbook createExcelWorkbook(List<SubledgerAlertDTO> alertSubledgers, String employeeName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        
        // Create sheet name based on employee name
        String sheetName = employeeName != null ? 
            "Credit Alerts - " + employeeName : "Credit Alert Report";
        
        // Limit sheet name to 31 characters (Excel limit)
        if (sheetName.length() > 31) {
            sheetName = sheetName.substring(0, 31);
        }
        
        Sheet sheet = workbook.createSheet(sheetName);
        
        // Create styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle percentageStyle = createPercentageStyle(workbook);
        CellStyle criticalPriorityStyle = createCriticalPriorityStyle(workbook);
        CellStyle highPriorityStyle = createHighPriorityStyle(workbook);
        CellStyle mediumPriorityStyle = createMediumPriorityStyle(workbook);
        CellStyle lowPriorityStyle = createLowPriorityStyle(workbook);
        CellStyle centeredStyle = createCenteredStyle(workbook);
        
        // Create header row - ADDED "S.No" column
        String[] headers = {
            "S.No", "Priority", "Customer Code", "Customer Name","Category", "Control Office", 
            "Salesperson", "Credit Limit (₹)", "Credit Days", 
            "Total Due (₹)", "Utilization %"
        };
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        int serialNo = 1;
        for (SubledgerAlertDTO subledger : alertSubledgers) {
            Row row = sheet.createRow(rowNum++);
            
            // Get priority based on NEW ranges
            int pct = subledger.getPercentage() != null ? subledger.getPercentage().intValue() : 0;
            String priority;
            CellStyle priorityStyle;
            
            // Apply NEW priority ranges matching your email format
            if (pct >= 150) {
                priority = "CRITICAL";
                priorityStyle = criticalPriorityStyle;
            } else if (pct >= 120) {
                priority = "HIGH";
                priorityStyle = highPriorityStyle;
            } else if (pct >= 100) {
                priority = "MEDIUM";
                priorityStyle = mediumPriorityStyle;
            } else {
                priority = "LOW";
                priorityStyle = lowPriorityStyle;
            }
            
            // Serial Number (S.No)
            Cell serialCell = row.createCell(0);
            serialCell.setCellValue(serialNo++);
            serialCell.setCellStyle(centeredStyle);
            
            // Priority
            Cell priorityCell = row.createCell(1);
            priorityCell.setCellValue(priority);
            priorityCell.setCellStyle(priorityStyle);
            
            // Customer Code
            row.createCell(2).setCellValue(getSafeString(subledger.getSubledgerCode()));
            
            // Customer Name
            row.createCell(3).setCellValue(getSafeString(subledger.getSubledgerName()));
            
         // Category
            row.createCell(4).setCellValue(getSafeString(subledger.getCategory()));
            
            // Control Office
            row.createCell(5).setCellValue(getSafeString(subledger.getCtrlOffice()));
            
            // Salesperson
            row.createCell(6).setCellValue(getSafeString(subledger.getSalesperson()));
            
            // Credit Limit
            Cell creditLimitCell = row.createCell(7);
            creditLimitCell.setCellValue(subledger.getCreditLimit() != null ? 
                subledger.getCreditLimit().doubleValue() : 0);
            creditLimitCell.setCellStyle(currencyStyle);
            
            // Credit Days
            Cell creditDaysCell = row.createCell(8);
            creditDaysCell.setCellValue(subledger.getCreditDays() != null ? 
                subledger.getCreditDays() : 0);
            creditDaysCell.setCellStyle(centeredStyle);
            
            // Total Due
            Cell totalDueCell = row.createCell(9);
            totalDueCell.setCellValue(subledger.getTotdue() != null ? 
                subledger.getTotdue().doubleValue() : 0);
            totalDueCell.setCellStyle(currencyStyle);
            
            // Utilization %
            Cell percentageCell = row.createCell(10);
            double percentageValue = subledger.getPercentage() != null ? 
                subledger.getPercentage().doubleValue() / 100.0 : 0.0;
            percentageCell.setCellValue(percentageValue);
            percentageCell.setCellStyle(percentageStyle);
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Add summary row if there are records
        if (alertSubledgers.size() > 0) {
            addSummaryRow(sheet, rowNum, alertSubledgers, workbook);
        }
        
        return workbook;
    }

    private void addSummaryRow(Sheet sheet, int rowNum, List<SubledgerAlertDTO> subledgers, Workbook workbook) {
        Row summaryRow = sheet.createRow(rowNum);
        
        // Calculate counts based on NEW priority ranges
        long criticalCount = subledgers.stream()
                .filter(s -> s != null && s.getPercentage() != null && s.getPercentage().intValue() >= 150)
                .count();
        
        long highCount = subledgers.stream()
                .filter(s -> s != null && s.getPercentage() != null && 
                           s.getPercentage().intValue() >= 120 && s.getPercentage().intValue() < 150)
                .count();
        
        long mediumCount = subledgers.stream()
                .filter(s -> s != null && s.getPercentage() != null && 
                           s.getPercentage().intValue() >= 100 && s.getPercentage().intValue() < 120)
                .count();
        
        long lowCount = subledgers.stream()
                .filter(s -> s != null && s.getPercentage() != null && 
                           s.getPercentage().intValue() >= 80 && s.getPercentage().intValue() < 100)
                .count();
        
        // Create summary style
        CellStyle summaryStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        summaryStyle.setFont(font);
        summaryStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        summaryStyle.setAlignment(HorizontalAlignment.CENTER);
        
        // Summary title
        Cell titleCell = summaryRow.createCell(0);
        titleCell.setCellValue("SUMMARY");
        titleCell.setCellStyle(summaryStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 10));
        
        // Next row for details
        Row detailRow = sheet.createRow(rowNum + 1);
        
        // Create detail style
        CellStyle detailStyle = workbook.createCellStyle();
        detailStyle.setAlignment(HorizontalAlignment.CENTER);
        
        // Add summary details
        detailRow.createCell(0).setCellValue("Total Customers:");
        detailRow.createCell(1).setCellValue(subledgers.size());
        
        detailRow.createCell(2).setCellValue("Critical (>150%):");
        detailRow.createCell(3).setCellValue(criticalCount);
        
        detailRow.createCell(4).setCellValue("High (120-150%):");
        detailRow.createCell(5).setCellValue(highCount);
        
        detailRow.createCell(6).setCellValue("Medium (100-120%):");
        detailRow.createCell(7).setCellValue(mediumCount);
        
        detailRow.createCell(8).setCellValue("Low (80-100%):");
        detailRow.createCell(9).setCellValue(lowCount);
        
        // Apply styles to detail row
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) { // Labels
                CellStyle labelStyle = workbook.createCellStyle();
                Font labelFont = workbook.createFont();
                labelFont.setBold(true);
                labelStyle.setFont(labelFont);
                detailRow.getCell(i).setCellStyle(labelStyle);
            } else { // Values
                CellStyle valueStyle = workbook.createCellStyle();
                valueStyle.setAlignment(HorizontalAlignment.CENTER);
                Font valueFont = workbook.createFont();
                valueFont.setBold(true);
                valueFont.setColor(IndexedColors.DARK_BLUE.getIndex());
                valueStyle.setFont(valueFont);
                detailRow.getCell(i).setCellStyle(valueStyle);
            }
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("₹#,##0.00"));
        return style;
    }

    private CellStyle createPercentageStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0%"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createCriticalPriorityStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createHighPriorityStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_RED.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createMediumPriorityStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.ORANGE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createLowPriorityStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createCenteredStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private ByteArrayResource convertWorkbookToResource(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    private ResponseEntity<?> createErrorResponse(String message) {
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO = createServiceResponseError(responseObjectsMap, message, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
    }

    // Helper methods
    private String getSafeString(String value) {
        return value != null && !value.trim().isEmpty() ? value : "N/A";
    }

    private String getSafeBigDecimal(BigDecimal value) {
        return value != null ? value.toString() : "0";
    }

    private String getSafeInteger(Integer value) {
        return value != null ? value.toString() : "0";
    }
}