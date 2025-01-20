package com.invoice.approval.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name="gst_empexpense")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpensesVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empexpensegen")
	@SequenceGenerator(name = "empexpensegen", sequenceName = "empexpenseseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "gst_empexpenseid")
	private Long id;
	
	@Column(name = "expensecategory", length = 50)
	private String expenseCategory;
	
	@Column(name = "expensename",length = 50)
	private String expenseName;
	
	@Column(name = "expensedate")
	private LocalDate expenseDate;
	
	@Column(name = "createdby",length = 50)
	private String createdBy;
	
	@Column(name = "modifiedby",length = 50)
	private String modifiedBy;
	
	@Column(name = "empcode",length = 10)
	private String empCode;
	
	@Column(name = "employee",length = 100)
	private String employee;
	
	@Column(name = "active")
	private String active="T";
	
	@Column(name = "cancel")
	private String cancel="F";
	
	@Column(name = "expenseamount",precision = 10,scale = 2)
	private BigDecimal expenseAmount;
	
	@OneToMany(mappedBy = "employeeExpensesVO",cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<EmployeeExpensesAttachmentVO> employeeExpensesAttachmentVO;
	
	@Embedded
	private CreatedUpdatedDate createdUpdatedDate = new CreatedUpdatedDate();

}
