package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocTypeMappingDetailsDTO {
	
	
	private String branch;
	private String branchCode;
	private int finYear;
	private String finYearId;
	private String screenCode;
	private String screenName;
	private String docCode;
	private String prefix;
	private int lastNo;
	

}

