package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBHSS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobHss {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hssSeq")  // ✅ Fixed
	@SequenceGenerator(name = "hssSeq", sequenceName = "SEQ_UT_HSS", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "HSS_ID")
    private Long hssId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "NAME", length = 200)
    private String name;

    @Column(name = "PARTY_ID", length = 50)
    private String partyId;

    @Column(name = "IEC", length = 50)
    private String iec;

    @Column(name = "BRANCH", length = 10)
    private String branch;

    @Column(name = "ADDRESS1", length = 500)
    private String address1;

    @Column(name = "ADDRESS2", length = 500)
    private String address2;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "STATE", length = 100)
    private String state;

    @Column(name = "PINCODE", length = 20)
    private String pincode;

    @Column(name = "COUNTRY", length = 50)
    private String country;

    @Column(name = "AD_CODE", length = 100)
    private String adCode;

    @Column(name = "PRECEDING_LEVEL", length = 10)
    private String precedingLevel;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}