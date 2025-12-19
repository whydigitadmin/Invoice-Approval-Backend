package com.invoice.approval.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gst_emailhistory")
public class EmailAlertHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_alert_seq")
    @SequenceGenerator(name = "email_alert_seq", sequenceName = "GST_EMAIL_ALERT_HISTORY_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "INVOICE_ID")
    private Long invoiceId;
    
    @Column(name = "VENDOR_ID")
    private Long vendorId;
    
    @Column(name = "VENDOR_NAME")
    private String vendorName;
    
    @Column(name = "MAIL_TO", nullable = false)
    private String mailTo;
    
    @Column(name = "CC_EMAILS", length = 1000)
    private String ccEmails;  // NEW FIELD
    
    @Column(name = "SALES_PERSON_NAME", length = 100)
    private String salesPersonName;  // NEW FIELD
    
    @Column(name = "CUSTOMER_COUNT")
    private Integer customerCount;  // NEW FIELD
    
    @Column(name = "ALERT_DATE")
    private LocalDateTime alertDate;
    
    @Column(name = "STATUS", length = 20)
    private String status; // SUCCESS, FAILED
    
    @Column(name = "ERROR_MESSAGE", length = 1000)
    private String errorMessage;
    
    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;
    
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
    
    @Column(name = "MODIFIED_BY", length = 50)
    private String modifiedBy;
    
    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;
    
    // Constructors
    public EmailAlertHistory() {
    }
    
    public EmailAlertHistory(Long invoiceId, Long vendorId, String vendorName, 
                           String mailTo, LocalDateTime alertDate, 
                           String status, String createdBy) {
        this.invoiceId = invoiceId;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.mailTo = mailTo;
        this.alertDate = alertDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdDate = LocalDateTime.now();
    }
    
    // Getters and Setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    
    public String getMailTo() { return mailTo; }
    public void setMailTo(String mailTo) { this.mailTo = mailTo; }
    
    public String getCcEmails() { return ccEmails; }
    public void setCcEmails(String ccEmails) { this.ccEmails = ccEmails; }
    
    public String getSalesPersonName() { return salesPersonName; }
    public void setSalesPersonName(String salesPersonName) { this.salesPersonName = salesPersonName; }
    
    public Integer getCustomerCount() { return customerCount; }
    public void setCustomerCount(Integer customerCount) { this.customerCount = customerCount; }
    
    public LocalDateTime getAlertDate() { return alertDate; }
    public void setAlertDate(LocalDateTime alertDate) { this.alertDate = alertDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
    
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (createdBy == null) {
            createdBy = "SYSTEM";
        }
        if (mailTo == null) {
            mailTo = "UNKNOWN";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "EmailAlertHistory{" +
                "id=" + id +
                ", vendorName='" + vendorName + '\'' +
                ", mailTo='" + mailTo + '\'' +
                ", alertDate=" + alertDate +
                ", status='" + status + '\'' +
                '}';
    }
}