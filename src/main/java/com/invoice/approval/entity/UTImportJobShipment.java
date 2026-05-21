package com.invoice.approval.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UT_IMPORTJOBSHIPMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UTImportJobShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipmentSeq")  // Changed
    @SequenceGenerator(name = "shipmentSeq", sequenceName = "SEQ_UT_SHIPMENT", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "SHIPMENT_ID")
    private Long shipmentId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "IGM_NO", length = 50)
    private String igmNo;

    @Column(name = "IGM_DATE")
    private LocalDateTime igmDate;

    @Column(name = "GATEWAY_NO", length = 50)
    private String gatewayNo;

    @Column(name = "GATEWAY_DATE")
    private LocalDateTime gatewayDate;

    @Column(name = "GATEWAY_PORT", length = 100)
    private String gatewayPort;

    @Column(name = "MAWB_MBL_NO", length = 50)
    private String mawbMblNo;

    @Column(name = "MAWB_MBL_DATE")
    private LocalDateTime mawbMblDate;

    @Column(name = "HAWB_HBL_NO", length = 50)
    private String hawbHblNo;

    @Column(name = "HAWB_HBL_DATE")
    private LocalDateTime hawbHblDate;

    @Column(name = "TOTAL_NO_OF_PACKAGES", length = 50)
    private String totalNoOfPackages;

    @Column(name = "PACKAGE_CODE", length = 20)
    private String packageCode;

    @Column(name = "GROSS_WEIGHT", length = 50)
    private String grossWeight;

    @Column(name = "WEIGHT_UOM", length = 10)
    private String weightUom;

    @Column(name = "MARKS_AND_NOS1", length = 500)
    private String marksAndNos1;

    @Column(name = "MARKS_AND_NOS2", length = 500)
    private String marksAndNos2;

    @Column(name = "MARKS_AND_NOS3", length = 500)
    private String marksAndNos3;

    @Column(name = "INWARD_DATE")
    private LocalDateTime inwardDate;

    @Column(name = "CARGO_TYPE", length = 50)
    private String cargoType;

    @Column(name = "ETA")
    private LocalDateTime eta;

    @Column(name = "BK_VESSEL_FLT", length = 50)
    private String bkVesselFlt;

    @Column(name = "BK_VOYAGE", length = 50)
    private String bkVoyage;

    @Column(name = "CARRIER_CODE", length = 50)
    private String carrierCode;

    @Column(name = "CHARGEABLE_WEIGHT", length = 50)
    private String chargeableWeight;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}