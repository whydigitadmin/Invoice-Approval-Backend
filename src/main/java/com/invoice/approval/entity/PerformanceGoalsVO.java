
package com.invoice.approval.entity;

import java.time.LocalDateTime;
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
@Data
@Table(name = "GST_PERFORMANCEGOALS")
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceGoalsVO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performancegoalsgen")
	@SequenceGenerator(name = "performancegoalshdrgen", sequenceName = "performancegoalsseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "GST_PERFORMANCEGOALSID")
	private Long id;

	@Column(name = "createdby", length = 50)
	private String createdBy;

	@Column(name = "modifiedby", length = 50)
	private String modifiedBy;

	@Column(name = "empcode", length = 10)
	private String empCode;

	@Column(name = "empname", length = 100)
	private String empName;

	@Column(name = "approve1", length = 100)
	private String approve1;

	@Column(name = "approve1name", length = 100)
	private String approve1name;

	@Column(name = "approve1on", length = 100)
	private LocalDateTime approve1on;

	@Column(name = "appraisalyear")
	private String appraisalYear;
	
	@Column(name = "pmonth")
	private String pmonth;
	
	@Column(name = "reportingto")
	private String reportingto;
	
	@Column(name = "reportingname")
	private String reportingname;

	@OneToMany(mappedBy = "performanceGoalsVO", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<PerformanceGoalsDtlVO> performanceGoalsDtlVO;

	@Embedded
	private CreatedUpdatedDate createdUpdatedDate = new CreatedUpdatedDate();

}
