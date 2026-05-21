package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBSUPPORTINGDOC")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobSupportingDoc {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supportingDocSeq")  // Changed
    @SequenceGenerator(name = "supportingDocSeq", sequenceName = "SEQ_UT_SUPPORTING_DOC", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "DOC_ID")
    private Long docId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "INVOICE_ID")
    private Long invoiceId;

    @Column(name = "LINE_ITEM_ID")
    private Long lineItemId;

    @Column(name = "DOC_LEVEL", length = 20)
    private String docLevel;

    @Column(name = "IRN_NO", length = 100)
    private String irnNo;

    @Column(name = "ICEGATE_ID", length = 50)
    private String icegateId;

    @Column(name = "SUBMITTED_BY", length = 100)
    private String submittedBy;

    @Column(name = "ISSUING_PARTY_NAME", length = 200)
    private String issuingPartyName;

    @Column(name = "BENEFICIARY_PARTY_NAME", length = 200)
    private String beneficiaryPartyName;

    @Column(name = "ISSUE_DATE")
    private LocalDateTime issueDate;

    @Column(name = "DRN_NO", length = 100)
    private String drnNo;

    @Column(name = "FILE_TYPE", length = 20)
    private String fileType;

    @Column(name = "ISSUING_PARTY_ID", length = 50)
    private String issuingPartyId;

    @Column(name = "ISSUING_PARTY_BRANCH", length = 10)
    private String issuingPartyBranch;

    @Column(name = "BENEFICIARY_PARTY_ID", length = 50)
    private String beneficiaryPartyId;

    @Column(name = "BENEFICIARY_PARTY_BRANCH", length = 10)
    private String beneficiaryPartyBranch;

    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    @Column(name = "ISSUE_PLACE", length = 200)
    private String issuePlace;

    @Column(name = "ISSUING_PARTY_ADDRESS1", length = 500)
    private String issuingPartyAddress1;

    @Column(name = "ISSUING_PARTY_ADDRESS2", length = 500)
    private String issuingPartyAddress2;

    @Column(name = "BENEFICIARY_PARTY_ADDRESS1", length = 500)
    private String beneficiaryPartyAddress1;

    @Column(name = "BENEFICIARY_PARTY_ADDRESS2", length = 500)
    private String beneficiaryPartyAddress2;

    @Column(name = "DOC_TYPE", length = 100)
    private String docType;

    @Column(name = "ISSUING_PARTY_CODE", length = 50)
    private String issuingPartyCode;

    @Column(name = "ISSUING_PARTY_CITY", length = 100)
    private String issuingPartyCity;

    @Column(name = "BENEFICIARY_PARTY_CODE", length = 50)
    private String beneficiaryPartyCode;

    @Column(name = "BENEFICIARY_PARTY_CITY", length = 100)
    private String beneficiaryPartyCity;

    @Column(name = "DOC_INFO", length = 1000)
    private String docInfo;

    @Column(name = "ISSUING_PARTY_PINCODE", length = 20)
    private String issuingPartyPincode;

    @Column(name = "BENEFICIARY_PARTY_PINCODE", length = 20)
    private String beneficiaryPartyPincode;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}