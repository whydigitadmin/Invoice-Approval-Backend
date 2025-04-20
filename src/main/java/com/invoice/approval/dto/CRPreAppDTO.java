package com.invoice.approval.dto;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class CRPreAppDTO {

	    private Long id;
	 
	    private String branchName;
	    
	    private String profoma;
	    
	    private String ptype;
	    
	    private String crRemarks;
	    
	    private String partyName;
	    
	    private String partyCode;
	    
	    private String vchNo;
	    
	    private String description;
	    
	    private String plImpact;
	    
	    private String documentsRequired;
	    
	    private LocalDate vchDt;
	    
	    private Double crAmt;
	    
	    private Double invAmt;
	    
	    private Double totChargeAmtLc;
	    
	    private Double totTaxAmtLc;
	    
	    private String reason;
	    
	    private String createdBy;
}
