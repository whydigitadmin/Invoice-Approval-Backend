package com.invoice.approval.controller;

import com.invoice.approval.dto.EmailAlertStatsDTO;
import com.invoice.approval.entity.EmailAlertHistory;
import com.invoice.approval.service.EmailAlertHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/email-alerts")
public class EmailAlertController {
    
    @Autowired
    private EmailAlertHistoryService emailAlertHistoryService;
    
    // **MAIN ENDPOINT - Use native query**
    @GetMapping("/daily-summary")
    public ResponseEntity<List<EmailAlertStatsDTO>> getDailySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
            LocalDateTime startDate) {
        
        // Default to 30 days if no start date provided
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        
        List<EmailAlertStatsDTO> stats = emailAlertHistoryService.getDailySummary(startDate);
        return ResponseEntity.ok(stats);
    }
    
    // Convenience endpoints
    @GetMapping("/last-7-days")
    public ResponseEntity<List<EmailAlertStatsDTO>> getLast7Days() {
        List<EmailAlertStatsDTO> stats = emailAlertHistoryService.getLastNDaysStatistics(7);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/last-30-days")
    public ResponseEntity<List<EmailAlertStatsDTO>> getLast30Days() {
        List<EmailAlertStatsDTO> stats = emailAlertHistoryService.getLastNDaysStatistics(30);
        return ResponseEntity.ok(stats);
    }
    
    // Get all email alerts
    @GetMapping
    public ResponseEntity<List<EmailAlertHistory>> getAllEmailAlerts() {
        List<EmailAlertHistory> alerts = emailAlertHistoryService.getAllEmailAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    // Get by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmailAlertHistory>> getByStatus(@PathVariable String status) {
        List<EmailAlertHistory> alerts = emailAlertHistoryService.getByStatus(status);
        return ResponseEntity.ok(alerts);
    }
}