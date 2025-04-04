package com.invoice.approval.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CRReasonDTO {
	
	  private Long id;
		 
	    private String code;
	    
	    private String crreason;
	    
	    private String description;
	    
	    private String plimpact;
	    
	    private String documentRequired;

}
