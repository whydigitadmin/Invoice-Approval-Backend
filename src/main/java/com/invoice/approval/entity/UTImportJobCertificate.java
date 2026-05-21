package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBCERTIFICATE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobCertificate {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "certificateSeq")  // ✅ Fixed
	@SequenceGenerator(name = "certificateSeq", sequenceName = "SEQ_UT_CERTIFICATE", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "CERTIFICATE_ID")

    private Long certificateId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "CERTIFICATE_NO", length = 50)
    private String certificateNo;

    @Column(name = "CERTIFICATE_TYPE", length = 50)
    private String certificateType;

    @Column(name = "CERTIFICATE_DATE")
    private LocalDateTime certificateDate;

    @Column(name = "COMMISSIONERATE", length = 200)
    private String commissionerate;

    @Column(name = "RANGE_NAME", length = 100)
    private String rangeName;

    @Column(name = "DIVISION", length = 100)
    private String division;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}