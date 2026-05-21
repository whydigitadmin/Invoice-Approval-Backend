package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBINVOICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobInvoice {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceSeq")  // ✅ Fixed
	@SequenceGenerator(name = "invoiceSeq", sequenceName = "SEQ_UT_INVOICE", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "INVOICE_ID")
    private Long invoiceId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "WAREHOUSE_INVOICE_SL_NO", length = 20)
    private String warehouseInvoiceSlNo;

    @Column(name = "INVOICE_NO", nullable = false, length = 50)
    private String invoiceNo;

    @Column(name = "INVOICE_DATE")
    private LocalDateTime invoiceDate;

    @Column(name = "INVOICE_VALUE", precision = 18, scale = 4)
    private Double invoiceValue;

    @Column(name = "INVOICE_CURRENCY", length = 50)
    private String invoiceCurrency;

    @Column(name = "TERMS_OF_INVOICE", length = 20)
    private String termsOfInvoice;

    @Column(name = "TOTAL_ASSESSABLE_VALUE", precision = 18, scale = 2)
    private Double totalAssessableValue;

    @Column(name = "TOTAL_DUTY", precision = 18, scale = 2)
    private Double totalDuty;

    @Column(name = "NATURE_OF_TRANSACTION", length = 50)
    private String natureOfTransaction;

    @Column(name = "VALUATION_METHOD", length = 100)
    private String valuationMethod;

    @Column(name = "PAYMENT_TERMS", length = 50)
    private String paymentTerms;

    @Column(name = "LC_NO", length = 50)
    private String lcNo;

    @Column(name = "LC_DATE")
    private LocalDateTime lcDate;

    @Column(name = "PO_NO", length = 50)
    private String poNo;

    @Column(name = "PO_DATE")
    private LocalDateTime poDate;

    @Column(name = "CONTRACT_NO", length = 50)
    private String contractNo;

    @Column(name = "CONTRACT_DATE")
    private LocalDateTime contractDate;

    @Column(name = "COMMON_DESCRIPTION", length = 500)
    private String commonDescription;

    @Column(name = "TOTAL_ITEM_VALUE", precision = 18, scale = 2)
    private Double totalItemValue;
    
    

    // Supplier
    @Column(name = "INV_SUPPLIER_NAME", length = 200)
    private String invSupplierName;

    @Column(name = "INV_SUPPLIER_PARTY_ID", length = 50)
    private String invSupplierPartyId;

    @Column(name = "INV_SUPPLIER_ADDRESS1", columnDefinition = "CLOB")
    private String invSupplierAddress1;

    @Column(name = "INV_SUPPLIER_CITY", length = 100)
    private String invSupplierCity;

    // Buyer
    @Column(name = "INV_BUYER_NAME", length = 200)
    private String invBuyerName;

    @Column(name = "INV_BUYER_PARTY_ID", length = 50)
    private String invBuyerPartyId;

    @Column(name = "INV_BUYER_ADDRESS1", columnDefinition = "CLOB")
    private String invBuyerAddress1;

    @Column(name = "INV_BUYER_CITY", length = 100)
    private String invBuyerCity;

    @Column(name = "INV_BUYER_COUNTRY", length = 50)
    private String invBuyerCountry;

    @Column(name = "INV_BUYER_ICEGATE_ID", length = 50)
    private String invBuyerIcegateId;

    // SVB Details
    @Column(name = "SVB_IS_RELATED", length = 10)
    private String svbIsRelated;

    @Column(name = "SVB_REF_NO", length = 50)
    private String svbRefNo;

    @Column(name = "SVB_DATE")
    private LocalDateTime svbDate;

    @Column(name = "SVB_FLAG", length = 50)
    private String svbFlag;

    @Column(name = "CHARGES_JSON", columnDefinition = "CLOB")
    private String chargesJson;

    @Column(name = "SUB_CHARGES_JSON", columnDefinition = "CLOB")
    private String subChargesJson;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
    
 // Add these fields to your UTImportJobInvoice entity class
    @Column(name = "svb_load_on_assessable_value")
    private Double svbLoadOnAssessableValue;

    @Column(name = "whether_load_on_assessable")
    private String whetherLoadOnAssessable;

    
}