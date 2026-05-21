package com.invoice.approval.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobInvoiceDTO {
    private Long invoiceId;
    private String invoiceNo;
    private LocalDateTime invoiceDate;
    private Double invoiceValue;
    private String invoiceCurrency;
    private String termsOfInvoice;
    private Double totalAssessableValue;
    private Double totalDuty;
    private String natureOfTransaction;
    private String paymentTerms;
    private String lcNo;
    private String poNo;
    private String invSupplierName;
    private String invBuyerName;
    private String chargesJson;
    private String subChargesJson;
    
    // SVB (Special Valuation Branch) fields
    private String svbIsRelated;
    private String svbRefNo;
    private LocalDateTime svbDate;
    private String svbFlag;
    private Double svbLoadOnAssessableValue;
    private String whetherLoadOnAssessable;
    
    private List<ImportJobLineItemDTO> lineItems;
}