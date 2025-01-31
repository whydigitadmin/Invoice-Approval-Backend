package com.invoice.approval.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name="gst_expemphdr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpensesVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empexphdrgen")
	@SequenceGenerator(name = "empexphdrgen", sequenceName = "empexphdrseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "gst_expemphdrid")
	private Long id;
	

	
	@Column(name = "createdby",length = 50)
	private String createdBy;
	
	@Column(name = "modifiedby",length = 50)
	private String modifiedBy;
	
	@Column(name = "empcode",length = 10)
	private String empCode;
	
	@Column(name = "empname",length = 100)
	private String empName;
	
	
	@Column(name = "approve1")
	private String approve1="F";
	
	
	
	@Column(name = "approve1name")
	private String approve1Name;
	
	@Column(name = "approve1on")
	private LocalDateTime approve1On;
	
	@Column(name = "visitfrom")
	private String visitFrom;
	
	@Column(name = "visitto")
	private String visitTo;
	
	@Column(name = "totamt",precision = 10,scale = 2)
	private BigDecimal totamt;
	
	@OneToMany(mappedBy = "employeeExpensesVO",cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<EmployeeExpensesAttachmentVO> employeeExpensesAttachmentVO;
	
	@Embedded
	private CreatedUpdatedDate createdUpdatedDate = new CreatedUpdatedDate();

}
