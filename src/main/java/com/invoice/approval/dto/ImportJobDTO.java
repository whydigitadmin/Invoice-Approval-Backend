package com.invoice.approval.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobDTO {
    private Long jobId;
    private String referenceId;
    private String organizationId;
    private String organizationName;
    private String jobType;
    private String jobNo;
    private LocalDateTime timeStamp;
    
    // Importer
    private String importerName;
    private String importerPartyId;
    private String importerIec;
    private String importerPanNo;
    private String importerAddress1;
    private String importerCity;
    private String importerState;
    private String importerCountry;
    private String importerIcegateId;
    
    // Job Info
    private String beNo;
    private LocalDateTime beDate;
    private String beType;
    private String customsHouseCode;
    private String mot;
    private Double totalAssessableValue;
    private Double totalDuty;
    private String portOfOrigin;
    private String countryOfOrigin;
    private String remarks;
    
    // Supplier
    private String supplierName;
    private String supplierPartyId;
    private String supplierAddress;
    
    // Exchange Rate
    private String currencyCode;
    private Double exchangeRate;
    
    private String createdBy;
    
    // Sub Objects
    private List<ImportJobTenantDTO> tenants;
    private List<ImportJobInvoiceDTO> invoices;
    private List<ImportJobShipmentDTO> shipments;
    private List<ImportJobBondDTO> bonds;
    private List<ImportJobCertificateDTO> certificates;
}