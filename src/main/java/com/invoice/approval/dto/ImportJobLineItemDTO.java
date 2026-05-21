package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobLineItemDTO {
    private Long lineItemId;
    private String partCode;
    private String invoiceDescription;
    private String customsDescription;
    private String hsn;
    private String brand;
    private String model;
    private String countryOfOrigin;
    private Double quantity;
    private String uom;
    private Double unitPrice;
    private Double amount;
    private Double assessableValue;
    private String notificationsJson;
    private String licenseJson;
}