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
	private String department;
	private String designation;
	private String lvl;
	private String reportingto;
	private String reportingtocode;
	private String active;
	private byte[] attachment;
	private String createdBy;
}
