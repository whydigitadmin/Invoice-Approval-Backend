package com.invoice.approval.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreGoalsDTO {

	private Long id;
	private String empCode;
	private String empName;
	private String createdBy;
	private String apprisalYear;
	private String approve1;
	private String approve1name;
	private String approve1on;
	private String reportingto;
	private String reportingname;
	
	private List<PreGoalsDtlDTO> preGoalsDtlDTO;
}



