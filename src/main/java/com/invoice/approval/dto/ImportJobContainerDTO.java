package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobContainerDTO {
    private String containerNo;
    private String sealNo;
    private String lclFcl;
    private String type;
    private String truckNo;
}