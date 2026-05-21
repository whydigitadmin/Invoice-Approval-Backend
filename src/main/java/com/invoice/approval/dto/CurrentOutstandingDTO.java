package com.invoice.approval.dto;

import java.math.BigDecimal;
import java.util.Date;

public class CurrentOutstandingDTO {
    private String subledgerCode;
    private String subledgerName;
    private String docId;
    private Date docDate;
    private String refNo;
    private Date refDate;
    private Integer creditDays;
    private BigDecimal creditLimit;
    private String ctrlOffice;
    private String salesperson;
    private BigDecimal totDue;
    
    // Email fields
    private String salespersonEmail;
    private String ccMail;
    private String category;
    private String partyCode;
    private String partyName;
    private String salespersonName;
    private String salespersonCode;

    // Default constructor
    public CurrentOutstandingDTO() {}

    // Parameterized constructor
    public CurrentOutstandingDTO(String subledgerCode, String subledgerName, String docId, 
                                Date docDate, String refNo, Date refDate, Integer creditDays, 
                                BigDecimal creditLimit, String ctrlOffice, String salesperson, 
                                BigDecimal totDue) {
        this.subledgerCode = subledgerCode;
        this.subledgerName = subledgerName;
        this.docId = docId;
        this.docDate = docDate;
        this.refNo = refNo;
        this.refDate = refDate;
        this.creditDays = creditDays;
        this.creditLimit = creditLimit;
        this.ctrlOffice = ctrlOffice;
        this.salesperson = salesperson;
        this.totDue = totDue;
    }

    // Getters and Setters
    public String getSubledgerCode() {
        return subledgerCode;
    }

    public void setSubledgerCode(String subledgerCode) {
        this.subledgerCode = subledgerCode;
    }

    public String getSubledgerName() {
        return subledgerName;
    }

    public void setSubledgerName(String subledgerName) {
        this.subledgerName = subledgerName;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public Date getRefDate() {
        return refDate;
    }

    public void setRefDate(Date refDate) {
        this.refDate = refDate;
    }

    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getCtrlOffice() {
        return ctrlOffice;
    }

    public void setCtrlOffice(String ctrlOffice) {
        this.ctrlOffice = ctrlOffice;
    }

    public String getSalesperson() {
        return salesperson;
    }

    public void setSalesperson(String salesperson) {
        this.salesperson = salesperson;
    }

    public BigDecimal getTotDue() {
        return totDue;
    }

    public void setTotDue(BigDecimal totDue) {
        this.totDue = totDue;
    }

    public String getSalespersonEmail() {
        return salespersonEmail;
    }

    public void setSalespersonEmail(String salespersonEmail) {
        this.salespersonEmail = salespersonEmail;
    }

    public String getCcMail() {
        return ccMail;
    }

    public void setCcMail(String ccMail) {
        this.ccMail = ccMail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPartyCode() {
        return partyCode;
    }

    public void setPartyCode(String partyCode) {
        this.partyCode = partyCode;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getSalespersonName() {
        return salespersonName;
    }

    public void setSalespersonName(String salespersonName) {
        this.salespersonName = salespersonName;
    }

    public String getSalespersonCode() {
        return salespersonCode;
    }

    public void setSalespersonCode(String salespersonCode) {
        this.salespersonCode = salespersonCode;
    }

    @Override
    public String toString() {
        return "CurrentOutstandingDTO{" +
                "subledgerCode='" + subledgerCode + '\'' +
                ", subledgerName='" + subledgerName + '\'' +
                ", docId='" + docId + '\'' +
                ", docDate=" + docDate +
                ", refNo='" + refNo + '\'' +
                ", refDate=" + refDate +
                ", creditDays=" + creditDays +
                ", creditLimit=" + creditLimit +
                ", ctrlOffice='" + ctrlOffice + '\'' +
                ", salesperson='" + salesperson + '\'' +
                ", totDue=" + totDue +
                ", salespersonEmail='" + salespersonEmail + '\'' +
                ", ccMail='" + ccMail + '\'' +
                ", category='" + category + '\'' +
                ", partyCode='" + partyCode + '\'' +
                ", partyName='" + partyName + '\'' +
                ", salespersonName='" + salespersonName + '\'' +
                ", salespersonCode='" + salespersonCode + '\'' +
                '}';
    }
}