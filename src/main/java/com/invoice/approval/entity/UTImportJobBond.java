package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBBOND")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobBond {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bondSeq")  // ✅ Fixed
    @SequenceGenerator(name = "bondSeq", sequenceName = "SEQ_UT_BOND", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "BOND_ID")
    private Long bondId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "BOND_NO", length = 50)
    private String bondNo;

    @Column(name = "BOND_CODE", length = 50)
    private String bondCode;

    @Column(name = "BOND_PORT", length = 100)
    private String bondPort;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}