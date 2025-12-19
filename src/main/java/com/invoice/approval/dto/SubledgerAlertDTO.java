package com.invoice.approval.dto;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SubledgerAlertDTO {
    private String subledgerCode;
    private String subledgerName;
    private String ctrlOffice;
    private String salesperson;
    private BigDecimal creditLimit;
    private Integer creditDays;
    private BigDecimal totdue;
    private BigDecimal percentage;
    private String mailid;
    private String ccmail;  // Field name
    private String employee;
    private String category;
    private Integer vendorId; // Add this field
    private Date alertDate;
    private Long totalEmails;
    private Long successfulEmails;
    private Long failedEmails;

    // Constructors
//    public SubledgerAlertDTO() {}
//
//    public SubledgerAlertDTO(String subledgerCode, String subledgerName,String category, String ctrlOffice, 
//                           String salesperson, BigDecimal creditLimit, Integer creditDays, 
//                           BigDecimal totdue, BigDecimal percentage) {
//        this.subledgerCode = subledgerCode;
//        this.subledgerName = subledgerName;
//        this.category = category;
//        this.ctrlOffice = ctrlOffice;
//        this.salesperson = salesperson;
//        this.creditLimit = creditLimit;
//        this.creditDays = creditDays;
//        this.totdue = totdue;
//        this.percentage = percentage;
//    }

    // Getters and Setters
//    public String getSubledgerCode() { return subledgerCode; }
//    public void setSubledgerCode(String subledgerCode) { this.subledgerCode = subledgerCode; }
//
//    public String getSubledgerName() { return subledgerName; }
//    public void setSubledgerName(String subledgerName) { this.subledgerName = subledgerName; }
//
//    public String getCtrlOffice() { return ctrlOffice; }
//    public void setCtrlOffice(String ctrlOffice) { this.ctrlOffice = ctrlOffice; }
//
//    public String getSalesperson() { return salesperson; }
//    public void setSalesperson(String salesperson) { this.salesperson = salesperson; }
//    
//    public String getCategory() { return category; }
//    public void setCategory(String category) { this.category = category; }
//
//    public BigDecimal getCreditLimit() { return creditLimit; }
//    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
//
//    public Integer getCreditDays() { return creditDays; }
//    public void setCreditDays(Integer creditDays) { this.creditDays = creditDays; }
//
//    public BigDecimal getTotdue() { return totdue; }
//    public void setTotdue(BigDecimal totdue) { this.totdue = totdue; }
//
//    public BigDecimal getPercentage() { return percentage; }
//    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
//    
//    public String getMailid() { return mailid; }
//    public void setMailid(String mailid) { this.mailid = mailid; }
//    
//    public Integer getVendorId() {
//        return vendorId;
//    }
//    
//    public void setVendorId(Integer vendorId) {
//        this.vendorId = vendorId;
//    }
//    
//    // CORRECTED: Getter should return ccmail, not mailid
//    public String getCcmail() { return ccmail; }  // Changed from getCcMail() to getCcmail()
//    public void setCcmail(String ccmail) { this.ccmail = ccmail; }
//
//    public String getEmployee() { return employee; }
//    public void setEmployee(String employee) { this.employee = employee; }  // Changed parameter name from mailid to employee
//    
//    public void EmailAlertStatsDTO(Object[] row) {
//        if (row.length > 0) this.alertDate = (Date) row[0];
//        if (row.length > 1) this.totalEmails = row[1] != null ? ((Number) row[1]).longValue() : 0L;
//        if (row.length > 2) this.successfulEmails = row[2] != null ? ((Number) row[2]).longValue() : 0L;
//        if (row.length > 3) this.failedEmails = row[3] != null ? ((Number) row[3]).longValue() : 0L;
//    }
//    
//    // Getters and setters
//    public Date getAlertDate() { return alertDate; }
//    public void setAlertDate(Date alertDate) { this.alertDate = alertDate; }
//    
//    public Long getTotalEmails() { return totalEmails; }
//    public void setTotalEmails(Long totalEmails) { this.totalEmails = totalEmails; }
//    
//    public Long getSuccessfulEmails() { return successfulEmails; }
//    public void setSuccessfulEmails(Long successfulEmails) { this.successfulEmails = successfulEmails; }
//    
//    public Long getFailedEmails() { return failedEmails; }
//    public void setFailedEmails(Long failedEmails) { this.failedEmails = failedEmails; }
}