package com.invoice.approval.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocTypeMappingDTO {
	
	
	private Long orgId;
	private String createdBy;
	private String branch;
	private String branchCode;
	private int finYear;
	private String finYearId;
	private List<DocTypeMappingDetailsDTO> docTypeMappingDetailsDTO;

}

