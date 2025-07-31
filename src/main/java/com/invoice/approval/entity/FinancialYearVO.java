package com.invoice.approval.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "financialyear")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialYearVO {

	@Id
	@Column(name = "financialyearid")
	private Long id;
	@Column(name = "finyridentifier")
	private Long finYrIdentifier;
	@Column(name = "finyr")
	private int finYear;
	@Column(name = "finyrid")
	private String finYrId;
	
}

