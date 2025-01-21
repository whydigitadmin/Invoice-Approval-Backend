package com.invoice.approval.dto;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpensesAttachmentDTO {
	
	    private String category;
	    private String expense;
	    private LocalDate expDate;
		private BigDecimal amount;

}
