package com.invoice.approval.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobShipmentDTO {
    private Long shipmentId;
    private String igmNo;
    private LocalDateTime igmDate;
    private String gatewayNo;
    private LocalDateTime gatewayDate;
    private String gatewayPort;
    private String mawbMblNo;
    private LocalDateTime mawbMblDate;
    private String hawbHblNo;
    private LocalDateTime hawbHblDate;
    private String totalNoOfPackages;
    private String packageCode;
    private String grossWeight;
    private String weightUom;
    private String marksAndNos1;
    private String marksAndNos2;
    private String marksAndNos3;
    private LocalDateTime inwardDate;
    private String cargoType;
    private LocalDateTime eta;
    private String bkVesselFlt;
    private String bkVoyage;
    private String carrierCode;
    private String chargeableWeight;
    
    // Initialize the list to avoid null
    private List<ImportJobContainerDTO> containers = new ArrayList<>();
}