package com.invoice.approval.dto;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class VendorDTO {
	
	private Long id;

    private String vendorname;


    private String address;


    private String gstin;
    

    private String contactperson;


    private String contactno;
    

    private String panno;
    

    private String emailid;
    
    private String createdBy;


}
