package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBSTATEMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobStatement {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statementSeq")  // Changed
    @SequenceGenerator(name = "statementSeq", sequenceName = "SEQ_UT_STATEMENT", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "STATEMENT_ID")
    private Long statementId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "INVOICE_ID")
    private Long invoiceId;

    @Column(name = "LINE_ITEM_ID")
    private Long lineItemId;

    @Column(name = "STATEMENT_LEVEL", length = 20)
    private String statementLevel;

    @Column(name = "STATEMENT_TYPE", length = 20)
    private String statementType;

    @Column(name = "STATEMENT_CODE", length = 20)
    private String statementCode;

    @Column(name = "STATEMENT_TEXT", length = 2000)
    private String statementText;

    @Column(name = "IS_MANDATORY", length = 10)
    private String isMandatory;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}