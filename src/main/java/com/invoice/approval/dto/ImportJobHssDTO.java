package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobHssDTO {
    private String name;
    private String partyId;
    private String iec;
    private String branch;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private String adCode;
    private String precedingLevel;
}