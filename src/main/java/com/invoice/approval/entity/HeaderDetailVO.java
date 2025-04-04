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
@Table(name = "GST_HEADERDETAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeaderDetailVO {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "headerdetailgen")
	@SequenceGenerator(name = "headerdetailgenseq", sequenceName = "headerdetailgenseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "GST_HEADERDETAILID")
	private Long id;

	@Column(name = "category")
	private String category;

	@Column(name = "description")
	private String description;

	@Column(name = "rate")
	private int rate;

	@Column(name = "qty")
	private int qty;

	@Column(name = "amount")
	private int amount;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "gst_headerid")
	private HeaderVO header;



}
