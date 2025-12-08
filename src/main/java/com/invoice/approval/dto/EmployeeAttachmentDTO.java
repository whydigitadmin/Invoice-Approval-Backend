package com.invoice.approval.dto;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public class EmployeeAttachmentDTO {
    private String employeeEmail;
    private String textFileName;
    private MultipartFile textFile;  // Changed from String to MultipartFile
    private String pdfFileName;
    private MultipartFile pdfFile;   // Changed from String to MultipartFile
    private String active;
    private LocalDateTime scheduledTime;
    private Boolean isScheduled;
    private String emailSubject;
    
    // Getters and Setters
    public String getEmployeeEmail() {
        return employeeEmail;
    }
    
    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }
    
    public String getTextFileName() {
        return textFileName;
    }
    
    public void setTextFileName(String textFileName) {
        this.textFileName = textFileName;
    }
    
    public MultipartFile getTextFile() {
        return textFile;
    }
    
    public void setTextFile(MultipartFile textFile) {
        this.textFile = textFile;
    }
    
    public String getPdfFileName() {
        return pdfFileName;
    }
    
    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }
    
    public MultipartFile getPdfFile() {
        return pdfFile;
    }
    
    public void setPdfFile(MultipartFile pdfFile) {
        this.pdfFile = pdfFile;
    }
    
    public String getActive() {
        return active;
    }
    
    public void setActive(String active) {
        this.active = active;
    }
    
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }
    
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public Boolean getIsScheduled() {
        return isScheduled;
    }
    
    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }
    
    public String getEmailSubject() {
        return emailSubject;
    }
    
    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }
}