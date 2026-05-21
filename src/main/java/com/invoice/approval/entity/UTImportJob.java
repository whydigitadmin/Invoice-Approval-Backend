package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import com.invoice.approval.dto.CreatedUpdatedDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOB")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJob {

	 @Id
	    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "importJobSeq")  // References importJobSeq
	    @SequenceGenerator(name = "importJobSeq", sequenceName = "SEQ_UT_IMPORTJOB", initialValue = 1000000001, allocationSize = 1)
	    @Column(name = "JOB_ID")
	    private Long jobId;

    @Column(name = "REFERENCE_ID", unique = true, nullable = false, length = 50)
    private String referenceId;

    @Column(name = "ORGANIZATION_ID", length = 30)
    private String organizationId;

    @Column(name = "ORGANIZATION_BRANCH_CODE", length = 10)
    private String organizationBranchCode;

    @Column(name = "ORGANIZATION_NAME", length = 200)
    private String organizationName;

    @Column(name = "JOB_TYPE", length = 20)
    private String jobType;

    @Column(name = "TIME_STAMP")
    private LocalDateTime timeStamp;
    
    @Column(name = "job_no")
    private String jobNo;

    // Importer Details
    @Column(name = "IMPORTER_NAME", length = 200)
    private String importerName;

    @Column(name = "IMPORTER_PARTY_ID", length = 50)
    private String importerPartyId;

    @Column(name = "IMPORTER_IEC", length = 50)
    private String importerIec;

    @Column(name = "IMPORTER_BRANCH", length = 10)
    private String importerBranch;

    @Column(name = "IMPORTER_PAN_NO", length = 20)
    private String importerPanNo;

    @Column(name = "IMPORTER_ADDRESS1", length = 4000)
    private String importerAddress1;

    @Column(name = "IMPORTER_ADDRESS2", length = 4000)
    private String importerAddress2;

    @Column(name = "IMPORTER_CITY", length = 100)
    private String importerCity;

    @Column(name = "IMPORTER_PINCODE", length = 20)
    private String importerPincode;

    @Column(name = "IMPORTER_STATE", length = 100)
    private String importerState;

    @Column(name = "IMPORTER_COUNTRY", length = 50)
    private String importerCountry;

    @Column(name = "IMPORTER_TYPE", length = 20)
    private String importerType;

    @Column(name = "IMPORTER_AD_CODE", length = 100)
    private String importerAdCode;

    @Column(name = "IMPORTER_ICEGATE_ID", length = 50)
    private String importerIcegateId;

    // Job Info
    @Column(name = "BE_NO", length = 50)
    private String beNo;

    @Column(name = "BE_DATE")
    private LocalDateTime beDate;

    @Column(name = "BE_TYPE", length = 50)
    private String beType;

    @Column(name = "CUSTOMS_HOUSE_CODE", length = 50)
    private String customsHouseCode;

    @Column(name = "MOT", length = 20)
    private String mot;

    @Column(name = "CUSTOMER_REF_NO", length = 100)
    private String customerRefNo;

    @Column(name = "TOTAL_ASSESSABLE_VALUE", precision = 18, scale = 2)
    private Double totalAssessableValue;

    @Column(name = "TOTAL_DUTY", precision = 18, scale = 2)
    private Double totalDuty;

    @Column(name = "PORT_OF_ORIGIN", length = 50)
    private String portOfOrigin;

    @Column(name = "COUNTRY_OF_ORIGIN", length = 50)
    private String countryOfOrigin;

    @Column(name = "UCR_NO", length = 50)
    private String ucrNo;

    @Column(name = "UCR_TYPE", length = 20)
    private String ucrType;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "PAYMENT_METHOD_CODE", length = 20)
    private String paymentMethodCode;

    // Supplier Details
    @Column(name = "SUPPLIER_NAME", length = 200)
    private String supplierName;

    @Column(name = "SUPPLIER_PARTY_ID", length = 50)
    private String supplierPartyId;

    @Column(name = "SUPPLIER_ADDRESS", columnDefinition = "CLOB")
    private String supplierAddress;

    @Column(name = "SUPPLIER_CITY", length = 100)
    private String supplierCity;

    @Column(name = "SUPPLIER_COUNTRY", length = 50)
    private String supplierCountry;

    // Exchange Rate
    @Column(name = "EX_RATE_EFF_DATE")
    private LocalDateTime exRateEffDate;

    @Column(name = "CURRENCY_CODE", length = 50)
    private String currencyCode;

    @Column(name = "EXCHANGE_RATE", precision = 18, scale = 6)
    private Double exchangeRate;

    @Column(name = "CERTIFICATE_NO", length = 100)
    private String certificateNo;

    // Warehouse Details
    @Column(name = "WAREHOUSE_BE_NO", length = 50)
    private String warehouseBeNo;

    @Column(name = "WAREHOUSE_BE_DATE")
    private LocalDateTime warehouseBeDate;

    @Column(name = "WAREHOUSE_JOB_NO", length = 50)
    private String warehouseJobNo;

    @Column(name = "WAREHOUSE_CODE", length = 100)
    private String warehouseCode;

    @Column(name = "WAREHOUSE_CUSTOMS_SITE_ID", length = 50)
    private String warehouseCustomsSiteId;

    @Column(name = "WAREHOUSE_PACKAGES_RELEASED", length = 50)
    private String warehousePackagesReleased;

    @Column(name = "WAREHOUSE_PACKAGE_CODE", length = 20)
    private String warehousePackageCode;

    @Column(name = "WAREHOUSE_GROSS_WEIGHT", length = 50)
    private String warehouseGrossWeight;

    @Column(name = "WAREHOUSE_UOM", length = 10)
    private String warehouseUom;

    // Commercial Tax
    @Column(name = "COMMERCIAL_TAX_REG_NO", length = 50)
    private String commercialTaxRegNo;

    @Column(name = "COMMERCIAL_TAX_TYPE", length = 50)
    private String commercialTaxType;

    @Column(name = "COMMERCIAL_TAX_STATE", length = 10)
    private String commercialTaxState;

    @Embedded
    private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();
}