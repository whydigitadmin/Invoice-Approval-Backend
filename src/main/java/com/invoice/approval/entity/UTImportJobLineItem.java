package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBLINEITEM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobLineItem {

	 @Id
	    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lineItemSeq")  // Changed
	    @SequenceGenerator(name = "lineItemSeq", sequenceName = "SEQ_UT_LINE_ITEM", initialValue = 1000000001, allocationSize = 1)
	    @Column(name = "LINE_ITEM_ID")
    private Long lineItemId;

    @Column(name = "INVOICE_ID", nullable = false)
    private Long invoiceId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "WAREHOUSE_ITEM_SL_NO", length = 20)
    private String warehouseItemSlNo;

    @Column(name = "PART_CODE", length = 100)
    private String partCode;

    @Column(name = "INVOICE_DESCRIPTION", length = 2000)
    private String invoiceDescription;

    @Column(name = "CUSTOMS_DESCRIPTION", length = 2000)
    private String customsDescription;

    @Column(name = "HSN", length = 20)
    private String hsn;

    @Column(name = "BRAND", length = 100)
    private String brand;

    @Column(name = "MODEL", length = 100)
    private String model;

    @Column(name = "COUNTRY_OF_ORIGIN", length = 50)
    private String countryOfOrigin;

    @Column(name = "QUANTITY", precision = 18, scale = 4)
    private Double quantity;

    @Column(name = "UOM", length = 20)
    private String uom;

    @Column(name = "CUS_QUANTITY", precision = 18, scale = 4)
    private Double cusQuantity;

    @Column(name = "CUS_UOM", length = 20)
    private String cusUom;

    @Column(name = "UNIT_PRICE", precision = 18, scale = 6)
    private Double unitPrice;

    @Column(name = "AMOUNT", precision = 18, scale = 4)
    private Double amount;

    @Column(name = "ASSESSABLE_VALUE", precision = 18, scale = 2)
    private Double assessableValue;

    @Column(name = "IGST_ASSESSABLE_VALUE", precision = 18, scale = 2)
    private Double igstAssessableValue;

    @Column(name = "NOTIFICATIONS_JSON", columnDefinition = "CLOB")
    private String notificationsJson;

    @Column(name = "LICENSE_JSON", columnDefinition = "CLOB")
    private String licenseJson;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}