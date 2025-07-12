package com.invoice.approval.entity;

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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MG_EMPLOYEEMASTER")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class EmployeeMasterVO {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empmasgen")
	@SequenceGenerator(name = "empmasgen", sequenceName = "empmasseq", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "MG_EMPLOYEEMASTERID")
    private Long id;
	
	  
	   @Column(name = "cancel", length = 1)
	    private String cancel="F";

	   @Column(name = "branch", length = 50)
	    private String branch;
	   
	   @Column(name = "employee", length = 50)
	    private String employee;

	   
	   @Column(name = "code", length = 50)
	    private String code;
	   
	   @Column(name = "dob", length = 50)
	    private LocalDate dob;
	   
	   @Column(name = "doj", length = 50)
	    private LocalDate doj;
	   
	   @Column(name = "department", length = 50)
	    private String department;
	   
	   @Column(name = "designation", length = 50)
	    private String designation;
	   
	   @Column(name = "lvl", length = 50)
	    private String lvl;
	   
	   @Column(name = "reportingto", length = 50)
	    private String reportingto;
	   
	   @Column(name = "reportingtocode", length = 50)
	    private String reportingtocode;
	   
	   @Column(name = "active", length = 50)
	    private String active;
	   
	    @Lob
		@Column(name = "attachment", columnDefinition="LONGBLOB")
		private byte[] attachment;
	    
	    
	    

		@Column(name = "createdby")
		private String createdBy;
		@Column(name = "modifiedby")
		private String updatedBy;

		
		@Embedded
		private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();
}
