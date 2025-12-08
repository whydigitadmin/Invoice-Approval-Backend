package com.invoice.approval.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class EmployeeBatchRequestDTO {
    private List<EmployeeAttachmentDTO> employees;
    
    private List<MultipartFile> files;
    
    // Getters and setters
    public List<EmployeeAttachmentDTO> getEmployees() {
        return employees;
    }
    
    public void setEmployees(List<EmployeeAttachmentDTO> employees) {
        this.employees = employees;
    }
}