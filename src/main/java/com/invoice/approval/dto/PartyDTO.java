package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PartyDTO {
	
	private Long id;

	private String partyName;

	private String partyCode;

	private String category;

	private String salesPerson;

	private String creditLimit;

	private String creditDays;

	private String ncreditLimit;

	private String ncreditDays;
	
	private String createdBy;
	
}
