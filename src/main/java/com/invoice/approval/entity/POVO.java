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

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GST_PO")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class POVO {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pogen")
	@SequenceGenerator(name = "pogenseq", sequenceName = "pogenseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "GST_POID")
	private Long id;

	@Column(name = "cancel", length = 1)
	private String cancel = "F";

	@Column(name = "createdby", length = 30)
	private String createdBy;

	@Column(name = "modifiedby", length = 30)
	private String modifiedBy;

	@Column(name = "poid", length = 30)
	private String poid;

	@Column(name = "podt", length = 30)
	private String podt;

	@Column(name = "branchname", length = 30)
	private String branchname;

	@Column(name = "entity", length = 100)
	private String entity;

	@Column(name = "vendor", length = 100)
	private String vendor;

	@Column(name = "vendoraddress", length = 300)
	private String vendoraddress;

	@Column(name = "remarks", length = 300)
	private String remarks;

	@Column(name = "partygstin", length = 20)
	private String partygstin;

	@Column(name = "billingstate", length = 20)
	private String billingstate;

	@Column(name = "shippingplace", length = 350)
	private String shippingplace;

	@Column(name = "gstin", length = 20)
	private String gstin;

	@Column(name = "approve", length = 30)
	private String approve;

	@Column(name = "approvedby", length = 30)
	private String approvedby;

	@Column(name = "approvedon", length = 30)
	private String approvedon;

	@Column(name = "bcurr", length = 30)
	private String bcurr;

	@Column(name = "exrate", length = 30)
	private float exrate;

	@Column(name = "quoterefno", length = 100)
	private String quoterefno;

	@Column(name = "addresstype", length = 30)
	private String addresstype;

	@Column(name = "shippingaddress", length = 300)
	private String shippingaddress;

	@Column(name = "terms", length = 1000)
	private String terms;

	@Column(name = "billamount", length = 30)
	private float billamount;

	

	@Column(name = "total")
	private int total;

	@OneToMany(mappedBy = "povo", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<PODTLVO> podtl;
	
	@Embedded
	private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();

}
