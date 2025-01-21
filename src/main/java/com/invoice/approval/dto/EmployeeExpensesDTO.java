package com.invoice.approval.dto;

import java.math.BigDecimal;
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
	private BigDecimal totamt;
	private String createdBy;
	  

}
