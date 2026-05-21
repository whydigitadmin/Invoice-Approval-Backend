package com.invoice.approval.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobSupportingDocDTO {
    private String irnNo;
    private String icegateId;
    private String submittedBy;
    private String issuingPartyName;
    private String beneficiaryPartyName;
    private LocalDateTime issueDate;
    private String drnNo;
    private String fileType;
    private String issuingPartyId;
    private String issuingPartyBranch;
    private String beneficiaryPartyId;
    private String beneficiaryPartyBranch;
    private LocalDateTime expiryDate;
    private String issuePlace;
    private String issuingPartyAddress1;
    private String issuingPartyAddress2;
    private String beneficiaryPartyAddress1;
    private String beneficiaryPartyAddress2;
    private String docType;
    private String issuingPartyCode;
    private String issuingPartyCity;
    private String beneficiaryPartyCode;
    private String beneficiaryPartyCity;
    private String docInfo;
    private String issuingPartyPincode;
    private String beneficiaryPartyPincode;
}