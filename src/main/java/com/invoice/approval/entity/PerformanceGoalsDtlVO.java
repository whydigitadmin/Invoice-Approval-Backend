
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
@Table(name = "gst_performancegoalsdtl")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PerformanceGoalsDtlVO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performancegoalsdtlgen")
	@SequenceGenerator(name = "performancegoalsdtlgen", sequenceName = "performancegoalsdtlgseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "gst_performancegoalsdtlid")
	private Long id;

	@Column(name = "perspective", length = 50)
	private String perspective;

	@Column(name = "objectivedesc")
	private String objectivedesc;

	@Column(name = "perassigned")
	private String perassigned;
	
	@Column(name = "measurement")
	private String measurement;
	
	@Column(name = "qtrtarget")
	private String qtrtarget;
	
	@Column(name = "performance")
	private String performance;
	
	@Column(name = "comments")
	private String comments;
	
	@Column(name = "performanceself")
	private String performanceself;
	
	@Column(name = "selfrating")
	private Long selfrating;
	
	@Column(name = "appraiserrating")
	private Long appraiserrating;
	
	@Column(name = "apprjustification")
	private String apprjustification;
	
	@ManyToOne
	@JoinColumn(name = "gst_performancegoalsid")
	@JsonBackReference
	private PerformanceGoalsVO performanceGoalsVO;

}
