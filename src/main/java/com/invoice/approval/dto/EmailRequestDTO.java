package com.invoice.approval.dto;

import java.util.List;

public class EmailRequestDTO {
    private List<String> employeeCodes;
    private String bccAddress;

    // Getters and setters
    public List<String> getEmployeeCodes() {
        return employeeCodes;
    }

    public void setEmployeeCodes(List<String> employeeCodes) {
        this.employeeCodes = employeeCodes;
    }

    public String getBccAddress() {
        return bccAddress;
    }

    public void setBccAddress(String bccAddress) {
        this.bccAddress = bccAddress;
    }
}