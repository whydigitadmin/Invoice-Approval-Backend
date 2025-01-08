package com.invoice.approval.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mg_partyhdr")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyMasterVO {


	@Id
	@Column(name = "mg_partyhdrid")
	private Long id;
	
	@Column(name = "partyCode")
	private String partyCode;

	@Column(name = "creditlimit")
	private String creditLimit;
	@Column(name = "creditdays")
	private String creditDays;

}
