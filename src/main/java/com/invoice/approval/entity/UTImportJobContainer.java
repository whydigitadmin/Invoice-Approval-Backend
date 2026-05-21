package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBCONTAINER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobContainer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "containerSeq")  // ✅ Fixed
	@SequenceGenerator(name = "containerSeq", sequenceName = "SEQ_UT_CONTAINER", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "CONTAINER_ID")
    private Long containerId;

    @Column(name = "SHIPMENT_ID", nullable = false)
    private Long shipmentId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "CONTAINER_NO", length = 50)
    private String containerNo;

    @Column(name = "SEAL_NO", length = 50)
    private String sealNo;

    @Column(name = "LCL_FCL", length = 10)
    private String lclFcl;

    @Column(name = "TYPE", length = 50)
    private String type;

    @Column(name = "TRUCK_NO", length = 50)
    private String truckNo;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}