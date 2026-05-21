package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobTenantDTO {
    private String tenantType;
    private String name;
    private String branch;
    private String licenseNo;
    private String addressLine;
    private String city;
    private String pincode;
    private String state;
    private String country;
    private String icegateId;
    private String customerBranch;
    private String submittedBy;
    private String aeoRegNo;
    private String aeoRole;
    private String transactionNo;
}