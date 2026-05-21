package com.invoice.approval.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "QUOTE_EMAIL_TRACKING")
public class QuoteEmailTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quote_tracking_seq")
    @SequenceGenerator(name = "quote_tracking_seq", sequenceName = "QUOTE_TRACKING_SEQ", allocationSize = 1)
    @Column(name = "TRACKING_ID")
    private Long id;
    
    @Column(name = "EMAIL_HASH", length = 512, unique = true, nullable = false)
    private String emailHash;
    
    @Column(name = "FROM_EMAIL", length = 255, nullable = false)
    private String fromEmail;
    
    @Column(name = "SUBJECT", length = 1000)
    private String subject;
    
    @Column(name = "POL", length = 50)
    private String pol;
    
    @Column(name = "POD", length = 50)
    private String pod;
    
    @Column(name = "FMODE", length = 50)
    private String mode;
    
    @Column(name = "FIRST_RECEIVED_DATE")
    private LocalDateTime firstReceivedDate;
    
    @Column(name = "LAST_REPLIED_DATE")
    private LocalDateTime lastRepliedDate;
    
    @Column(name = "IS_REPLIED", length = 1)
    private String isReplied = "F";
    
    @Column(name = "REPLY_COUNT")
    private Integer replyCount = 0;
    
    @Column(name = "REPLY_STATUS", length = 50)
    private String replyStatus;
    
    @Column(name = "ERROR_MESSAGE", length = 4000)
    private String errorMessage;
    
    // Constructors
    public QuoteEmailTracking() {
        this.firstReceivedDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmailHash() { return emailHash; }
    public void setEmailHash(String emailHash) { this.emailHash = emailHash; }
    
    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getPol() { return pol; }
    public void setPol(String pol) { this.pol = pol; }
    
    public String getPod() { return pod; }
    public void setPod(String pod) { this.pod = pod; }
    
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    
    public LocalDateTime getFirstReceivedDate() { return firstReceivedDate; }
    public void setFirstReceivedDate(LocalDateTime firstReceivedDate) { this.firstReceivedDate = firstReceivedDate; }
    
    public LocalDateTime getLastRepliedDate() { return lastRepliedDate; }
    public void setLastRepliedDate(LocalDateTime lastRepliedDate) { this.lastRepliedDate = lastRepliedDate; }
    
    public String getIsReplied() { return isReplied; }
    public void setIsReplied(String isReplied) { this.isReplied = isReplied; }
    
    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
    
    public String getReplyStatus() { return replyStatus; }
    public void setReplyStatus(String replyStatus) { this.replyStatus = replyStatus; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}