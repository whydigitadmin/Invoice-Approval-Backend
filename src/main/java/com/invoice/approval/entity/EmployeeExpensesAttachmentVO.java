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
@Table(name="gst_expempdtl")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpensesAttachmentVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empexpdtlgen")
	@SequenceGenerator(name = "empexpattachgen", sequenceName = "empexpdtlgseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "gst_expempdtlid")
	private Long id;
	
	
	 @Column(name = "category", length = 50)
	    private String category;
	    
	    @Column(name = "expense",length = 50)
	    private String expense;
	    
	    @Column(name = "expdate")
	    private LocalDate expDate;
	    
		@Column(name = "empcde",length = 10)
		private String empCde;
		
		@Column(name = "empname",length = 100)
		private String empName;
		

		@Column(name = "amount",precision = 10,scale = 2)
		private BigDecimal amount;
	    
	@Column(name = "filename")
	private String fileName;
	
	@Lob
    @Column(name = "attachment", columnDefinition="BLOB")
    private byte[] attachment;
	
	@ManyToOne
	@JoinColumn(name = "gst_expemphdrid")
	@JsonBackReference
	private EmployeeExpensesVO employeeExpensesVO;
	
	
	

}
