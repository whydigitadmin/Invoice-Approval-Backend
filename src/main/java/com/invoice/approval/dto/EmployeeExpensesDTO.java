package com.invoice.approval.dto;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpensesDTO {
	
	private Long id;
	private String expenseCategory;
	private String expenseName;
	private LocalDate expenseDate;
	private String createdBy;
	private BigDecimal expenseAmount;
	private String empCode;
	private String employee;

}
