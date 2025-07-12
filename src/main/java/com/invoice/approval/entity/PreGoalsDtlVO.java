package com.invoice.approval.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gst_pregoalsdtl")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PreGoalsDtlVO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pregoalsdtlgen")
	@SequenceGenerator(name = "pregoalsdtlgen", sequenceName = "pregoalsdtlgseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "gst_pregoalsdtlid")
	private Long id;

	@Column(name = "area", length = 50)
	private String area;

	@Column(name = "goals")
	private String goals;

	@Column(name = "selfinput")
	private String selfinput;
	
	@Column(name = "rating")
	private String rating;
	
	@Column(name = "score")
	private Long score;
	
	@ManyToOne
	@JoinColumn(name = "gst_pregoalsid")
	@JsonBackReference
	private PreGoalsVO preGoalsVO;

}
