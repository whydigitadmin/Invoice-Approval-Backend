package com.invoice.approval.dto;

public class QuoteEmailRequestDTO {
    private String fromEmail;
    private String subject;
    private String pol;
    private String pod;
    private String mode;
    private String content;
    
    // Getters and Setters
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
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}