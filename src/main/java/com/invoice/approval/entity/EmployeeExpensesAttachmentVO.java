package com.invoice.approval.entity;

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
@Table(name="gst_empexpattach")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpensesAttachmentVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empexpattachgen")
	@SequenceGenerator(name = "empexpattachgen", sequenceName = "empexpattachgseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "gst_empexpattachid")
	private Long id;
	
	@Column(name = "filename")
	private String fileName;
	
	@Lob
    @Column(name = "attachment", columnDefinition="BLOB")
    private byte[] attachment;
	
	@ManyToOne
	@JoinColumn(name = "gst_empexpenseid")
	@JsonBackReference
	private EmployeeExpensesVO employeeExpensesVO;
	
	
	

}
