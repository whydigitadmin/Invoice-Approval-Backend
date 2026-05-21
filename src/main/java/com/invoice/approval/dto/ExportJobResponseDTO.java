package com.invoice.approval.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class ExportJobResponseDTO {
    private Long jobId;
    private String referenceId;
    private String organizationId;
    private String organizationBranchCode;
    private String organizationName;
    private String jobType;
    private Instant timeStamp;
    private String tenantName;
    private String tenantBranch;
    private String jobNo;
    private String mot;
    private String customsHouseCode;
    private String exporterName;
    private String consigneeName;
    private String portOfLoading;
    private Double grossWeight;
    private Double netWeight;
    private String weightUom;
    private String createdBy;
    private String createdOn;
    // Add all other fields you want to return
}