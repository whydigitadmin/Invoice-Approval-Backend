package com.invoice.approval.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class ExportJobRequestDTO {
    private Job job;
    
    @Data
    public static class Job {
        private HeaderDetails headerDetails;
        private ShipmentDetails shipmentDetails;
    }
    
    @Data
    public static class HeaderDetails {
        private Instant timeStamp;
        private String organizationId;
        private String organizationBranchCode;
        private String organizationName;
        private String jobNo;        // ADD THIS FIELD
        private String jobDate;
        private String jobType;
        private String referenceId;
    }
    
    @Data
    public static class ShipmentDetails {
        private JobDetails jobDetails;
        private List<InvoiceDetail> invoiceDetails;
    }
    
    @Data
    public static class JobDetails {
        private TenantDetails tenants;
        private PartyDetails exporter;
        private PartyDetails consignee;
        private JobInfo jobInfo;
        private ShipmentInfo shipmentDetails;
        private CargoInfo cargoDetails;  // ← This already has all fields
    }
    
    @Data
    public static class TenantDetails {
        private TenantInfo cb;
    }
    
    @Data
    public static class TenantInfo {
        private String name;
        private String branch;
    }
    
    @Data
    public static class PartyDetails {
        private String name;
        private String partyId;
        private String panNo;
        private String branch;
        private String address1;
        private String address2;
        private String city;
        private String pincode;
        private String state;
        private String country;
    }
    
    @Data
    public static class JobInfo {
        private String jobNo;
        private String jobType;
        private String mot;
        private String customsHouseCode;
        private String customerRefNo;
    }
    
    @Data
    public static class ShipmentInfo {
        private String portOfLoading;
        private String portOfDischarge;
        private String countryOfDischarge;
        private String portOfFinalDestination;
        private String countryOfFinalDestination;
        private String hawbHblNo;
    }
    
    @Data
    public static class CargoInfo {
        private String grossWeight;
        private String netWeight;
        private String uom;              // ← This maps to weightUom
        private String chargeableWeight;
        private String noOfPackages;
        private String packageCode;
        private String noOfContainers;
    }
    
    @Data
    public static class InvoiceDetail {
        private InvoiceInfo invoiceInfo;
        private CargoDetails cargoDetails;
        private PartyDetails buyer;
        private PartyDetails exporter;
    }
    
    @Data
    public static class InvoiceInfo {
        private String invoiceNo;
        private Instant invoiceDate;
        private String invoiceValue;
        private String invoiceCurrency;
        private String natureOfContract;
    }
    
    @Data
    public static class CargoDetails {
        private String totalGrossWeight;
        private String totalNetWeight;
        private String uom;
        private String noOfPackages;
        private String packageCode;
    }
}