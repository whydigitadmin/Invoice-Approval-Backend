package com.invoice.approval.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mg_partyupdate")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyHdrVO {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "partyrhdrgen")
	@SequenceGenerator(name = "partyrhdrgen", sequenceName = "partyrhdrseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "mg_partyupdateid")
	private Long id;

	@Column(name = "partyname")
	private String partyName;
	@Column(name = "partycode")
	private String partyCode;
	@Column(name = "category")
	private String category;
	@Column(name = "salesperson")
	private String salesPerson;
	@Column(name = "creditlimit")
	private String creditLimit;
	@Column(name = "creditdays")
	private String creditDays;
	@Column(name = "ncreditlimit")
	private String ncreditLimit;
	@Column(name = "ncreditdays")
	private String ncreditDays;
	
	@Column(name = "createdby")
	private String createdBy;
	@Column(name = "modifiedby")
	private String updatedBy;

	
	@Embedded
	private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();
	
	
	
	



}
