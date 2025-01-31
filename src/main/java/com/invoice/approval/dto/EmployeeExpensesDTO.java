package com.invoice.approval.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpensesDTO {
	
	private Long id;
	private String empCode;
	private String empName;
	private String createdBy;
	private String visitFrom;
	private String visitTo;
	private List<EmployeeExpensesAttachmentDTO> employeeExpensesAttachmentDTO;
}
