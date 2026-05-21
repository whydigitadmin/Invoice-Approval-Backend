package com.invoice.approval.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobCertificateDTO {
    private String certificateNo;
    private String certificateType;
    private LocalDateTime certificateDate;
    private String commissionerat;
    private String rangeName;
    private String division;
}