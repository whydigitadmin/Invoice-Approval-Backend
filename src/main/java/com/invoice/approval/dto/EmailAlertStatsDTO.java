package com.invoice.approval.dto;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class EmailAlertStatsDTO {
    private LocalDate alertDate;
    private Long totalEmails;
    private Long successfulEmails;
    private Long failedEmails;
    
    // Empty constructor
    public EmailAlertStatsDTO() {
    }
    
    // Constructor for projection
    public EmailAlertStatsDTO(Date alertDate, Long totalEmails, 
                              Long successfulEmails, Long failedEmails) {
        this.alertDate = alertDate != null ? 
            alertDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        this.totalEmails = totalEmails != null ? totalEmails : 0L;
        this.successfulEmails = successfulEmails != null ? successfulEmails : 0L;
        this.failedEmails = failedEmails != null ? failedEmails : 0L;
    }
    
    // Constructor for Object[] from native query
    public EmailAlertStatsDTO(Object[] result) {
        if (result != null && result.length >= 4) {
            // Oracle returns java.sql.Date/Timestamp
            if (result[0] instanceof java.sql.Timestamp) {
                this.alertDate = ((java.sql.Timestamp) result[0]).toLocalDateTime().toLocalDate();
            } else if (result[0] instanceof java.sql.Date) {
                this.alertDate = ((java.sql.Date) result[0]).toLocalDate();
            } else if (result[0] instanceof Date) {
                this.alertDate = ((Date) result[0]).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
            
            this.totalEmails = convertToLong(result[1]);
            this.successfulEmails = convertToLong(result[2]);
            this.failedEmails = convertToLong(result[3]);
        }
    }
    
    private Long convertToLong(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    // Getters and Setters
    public LocalDate getAlertDate() {
        return alertDate;
    }
    
    public void setAlertDate(LocalDate alertDate) {
        this.alertDate = alertDate;
    }
    
    public Long getTotalEmails() {
        return totalEmails != null ? totalEmails : 0L;
    }
    
    public void setTotalEmails(Long totalEmails) {
        this.totalEmails = totalEmails;
    }
    
    public Long getSuccessfulEmails() {
        return successfulEmails != null ? successfulEmails : 0L;
    }
    
    public void setSuccessfulEmails(Long successfulEmails) {
        this.successfulEmails = successfulEmails;
    }
    
    public Long getFailedEmails() {
        return failedEmails != null ? failedEmails : 0L;
    }
    
    public void setFailedEmails(Long failedEmails) {
        this.failedEmails = failedEmails;
    }
    
    // Helper methods
    public Double getSuccessRate() {
        long total = getTotalEmails();
        if (total == 0) return 0.0;
        return (getSuccessfulEmails().doubleValue() / total) * 100;
    }
    
    @Override
    public String toString() {
        return String.format("EmailAlertStatsDTO{alertDate=%s, total=%d, success=%d, failed=%d, successRate=%.2f%%}",
                alertDate, getTotalEmails(), getSuccessfulEmails(), getFailedEmails(), getSuccessRate());
    }
}