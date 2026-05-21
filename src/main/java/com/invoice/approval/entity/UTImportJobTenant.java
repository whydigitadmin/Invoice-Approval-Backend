package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBTENANT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobTenant {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tenantSeq")  // Changed
    @SequenceGenerator(name = "tenantSeq", sequenceName = "SEQ_UT_TENANT", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "TENANT_ID")
    private Long tenantId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "TENANT_TYPE", length = 20)
    private String tenantType;

    @Column(name = "NAME", length = 200)
    private String name;

    @Column(name = "BRANCH", length = 10)
    private String branch;

    @Column(name = "LICENSE_NO", length = 50)
    private String licenseNo;

    @Column(name = "ADDRESS_LINE", columnDefinition = "CLOB")
    private String addressLine;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "PINCODE", length = 20)
    private String pincode;

    @Column(name = "STATE", length = 100)
    private String state;

    @Column(name = "COUNTRY", length = 50)
    private String country;

    @Column(name = "ICEGATE_ID", length = 50)
    private String icegateId;

    @Column(name = "CUSTOMER_BRANCH", length = 50)
    private String customerBranch;

    @Column(name = "SUBMITTED_BY", length = 100)
    private String submittedBy;

    @Column(name = "AEO_REG_NO", length = 50)
    private String aeoRegNo;

    @Column(name = "AEO_ROLE", length = 50)
    private String aeoRole;

    @Column(name = "TRANSACTION_NO", length = 200)
    private String transactionNo;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}