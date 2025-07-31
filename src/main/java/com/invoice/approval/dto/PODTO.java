package com.invoice.approval.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PODTO {
	private Long id;

	private String createdBy;

	private String poid;

	private String podt;
	private int finYear;
	
	private String entity;
	private String branchname;
	private String vendor;
	private String vendoraddress;
	private String partygstin;
	private String gstin;
	private String remarks;
	private String approve;
	private String approvedby;
	private String approvedon;
	private String bcurr;
	private String billingstate;
	private String shippingplace;
	private float exrate;
	private String quoterefno;
	private float billamount;
	private float lamount;
	private String addresstype;
	private String shippingaddress;
	private String terms; 
	
	

	private List<PODTLDTO> podtlDto;



}
