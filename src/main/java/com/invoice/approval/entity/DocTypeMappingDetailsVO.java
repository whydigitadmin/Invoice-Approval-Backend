package com.invoice.approval.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="documenttypemappingdetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocTypeMappingDetailsVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documenttypemappingdetailsgen")
	@SequenceGenerator(name = "documenttypemappingdetailsgen", sequenceName = "documenttypemappingdetailsseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "documenttypemappingdetailsid")
	private Long id;
	
	@Column(name="orgid")
	private Long orgId;
	
	@Column(name="branch")
	private String branch;
	
	@Column(name="branchcode")
	private String branchCode;
	
	@Column(name="finyear")
	private int finYear;
	
	@Column(name="finyearid")
	private String finYearId;
	
	@Column(name="screencode")
	private String screenCode;
	
	@Column(name="screenname")
	private String screenName;
	
	@Column(name="doccode")
	private String docCode;
	
	@Column(name="prefix")
	private String prefix;
	
	@Column(name="lastno")
	private int lastNo;
	
	@ManyToOne
	@JoinColumn(name="documenttypemappingid")
	@JsonBackReference
	private DocTypeMappingVO docTypeMappingVO;
	

}


