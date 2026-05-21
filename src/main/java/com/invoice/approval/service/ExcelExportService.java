package com.invoice.approval.service;

import com.invoice.approval.dto.CurrentOutstandingDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class ExcelExportService {

    public byte[] generateOutstandingExcel(List<CurrentOutstandingDTO> outstandingList, String salespersonName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pending Invoices");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Create headers
            String[] headers = {"S.No", "Customer Code", "Customer Name", "Invoice No", 
                               "Invoice Date", "Ref No", "Ref Date", "Age (Days)", 
                               "Credit Limit", "Branch", "Amount Due"};
            
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Add data rows
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            int rowNum = 1;
            
            for (CurrentOutstandingDTO item : outstandingList) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(rowNum - 1); // S.No
                row.createCell(1).setCellValue(getSafeString(item.getSubledgerCode()));
                row.createCell(2).setCellValue(getSafeString(item.getSubledgerName()));
                row.createCell(3).setCellValue(getSafeString(item.getDocId()));
                row.createCell(4).setCellValue(item.getDocDate() != null ? dateFormat.format(item.getDocDate()) : "");
                row.createCell(5).setCellValue(getSafeString(item.getRefNo()));
                row.createCell(6).setCellValue(item.getRefDate() != null ? dateFormat.format(item.getRefDate()) : "");
                row.createCell(7).setCellValue(item.getCreditDays() != null ? item.getCreditDays() : 0);
                row.createCell(8).setCellValue(item.getCreditLimit() != null ? item.getCreditLimit().doubleValue() : 0);
                row.createCell(9).setCellValue(getSafeString(item.getCtrlOffice()));
                row.createCell(10).setCellValue(item.getTotDue() != null ? item.getTotDue().doubleValue() : 0);

                // Apply data style to all cells in this row
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String getSafeString(String value) {
        return value != null && !value.trim().isEmpty() ? value : "";
    }
}