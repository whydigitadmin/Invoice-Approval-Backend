package com.invoice.approval.dto;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMasterDTO {
	
	private Long id;
	private String branch;
	private String employee;
	private String code;
	private LocalDate dob;
	private LocalDate doj;
	private String mobile;
	private String department;
	private String designation;
	private String mailid;
	private String lvl;
	private String reportingto;
	private String reportingtocode;
	private String subdepartment;
	private String vertical;
	private String costcenter;
	private String branchhead;
	private String regionalhead;
	private String verticalhead; 
	private String corpteam;
	private String active;
	private byte[] attachment;
	private String createdBy;
}
