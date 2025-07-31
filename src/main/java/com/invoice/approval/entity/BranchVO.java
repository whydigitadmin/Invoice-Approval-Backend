package com.invoice.approval.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mg_branchhdr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchVO {

	@Id
	@Column(name = "mg_branchhdrid")
	private Long id;
	@Column(name = "branchname")
	private String branch;
	@Column(name = "branchcode")
	private String branchCode;
}
