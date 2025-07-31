package com.invoice.approval.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="documenttype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocTypeVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documenttypegen")
	@SequenceGenerator(name = "documenttypegen", sequenceName = "documenttypeseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "documenttypeid")
	private Long id;
	
	@Column(name="screencode")
	private String screenCode;
	
	@Column(name="screenname")
	private String screenName;
	
	@Column(name="doccode")
	private String docCode;
	
	@Column(name="orgid")
	private String orgId;
	
	@Column(name="doccodepos")
	private int docCodePos;
	
	@Column(name="branchcodepos")
	private int branchCodePos;
	
	@Column(name="finyearpos")
	private int finYearPos;
	
	@Column(name="seqpos")
	private int seqPos;
	
	@Column(name="seqdigit")
	private int seqDigit;

    @Column(name="codepattern",nullable = false)
    private String codePattern; // e.g. ${companyCode}-${department}-${seq}

}



