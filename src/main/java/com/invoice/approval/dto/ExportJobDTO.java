package com.invoice.approval.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportJobDTO {
    private Long jobId;
    private String referenceId;
    private String organizationId;
    private String organizationBranchCode;
    private String organizationName;
    private String jobType;
    private Instant timeStamp;  // Change from LocalDateTime to Instant
    
    // Tenant
    private String tenantName;
    private String tenantBranch;
    
    // Exporter
    private String exporterName;
    private String exporterPartyId;
    private String exporterPanNo;
    private String exporterAddress1;
    private String exporterAddress2;
    private String exporterCity;
    private String exporterPincode;
    private String exporterState;
    private String exporterCountry;
    
    // Consignee
    private String consigneeName;
    private String consigneeBranch;
    private String consigneeAddress1;
    private String consigneeAddress2;
    private String consigneeCity;
    private String consigneePincode;
    private String consigneeState;
    private String consigneeCountry;
    
    // Job Info
    private String jobNo;
    private String jobInfoJobType;
    private String mot;
    private String customsHouseCode;
    private String customerRefNo;
    
    // Shipment
    private String portOfLoading;
    private String portOfDischarge;
    private String countryOfDischarge;
    private String portOfFinalDestination;
    private String countryOfFinalDestination;
    private String hawbHblNo;
    
    // Cargo - Use appropriate types
    private Double grossWeight;      // Change from String to Double
    private Double netWeight;        // Change from String to Double
    private String weightUom;
    private Double chargeableWeight; // Change from String to Double
    private Integer noOfPackages;    // Change from String to Integer
    private String packageCode;
    private Integer noOfContainers;  // Change from String to Integer
    
    // Invoice
    private String invoiceNo;
    private Instant invoiceDate;     // Change from LocalDateTime to Instant
    private Double invoiceValue;
    private String invoiceCurrency;
    private String natureOfContract;
    private String invoiceTotalGrossWeight;
    private String invoiceTotalNetWeight;
    private String invoiceWeightUom;
    private String invoiceNoOfPackages;
    private String invoicePackageCode;
    private String invoiceBuyerName;
    private String invoiceBuyerAddress1;
    private String invoiceBuyerCity;
    private String invoiceExporterName;
    
    private String createdBy;
}