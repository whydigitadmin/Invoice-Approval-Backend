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
@Table(name = "GST_PODTL")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class PODTLVO {




	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "podetailgen")
	@SequenceGenerator(name = "podetailgenseq", sequenceName = "podetailgenseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "GST_PODTLID")
	private Long id;

	@Column(name = "item")
	private String item;

	@Column(name = "description")
	private String description;
	
	@Column(name = "basecurr")
	private String basecurr;

	@Column(name = "exrate")
	private float exrate;

	@Column(name = "rate")
	private float rate;
	
	@Column(name = "price")
	private float price;

	@Column(name = "qty")
	private int qty;

	@Column(name = "lcamount")
	private float lcamount;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "gst_poid")
	private POVO povo;




}
