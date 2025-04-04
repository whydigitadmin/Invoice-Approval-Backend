package com.invoice.approval.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ut_expenseupload")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class ExpenseUploadVO {
	
		
		
		@SuppressWarnings("unused")
		private static final long serialVersionUID = 1L;

		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expuploadgen")
		@SequenceGenerator(name = "expuploadgenseq", sequenceName = "expuploadgenseq", initialValue = 1000000001, allocationSize = 1)
	    @Column(name = "ut_expenseuploadID")
	    private Long id;
		

		  @Column(name = "category", length = 50)
		    private String category;
		  
		   @Column(name = "cancel", length = 1)
		    private String cancel="F";

		    @Column(name = "description", length = 30)
		    private String description;

		    @Column(name = "rate", length = 10)
		    private BigDecimal rate;

		    @Column(name = "total", length = 100)
		    private BigDecimal total;

		    			

		    		
			@Column(name = "createdby")
			private String createdBy;
			@Column(name = "modifiedby")
			private String updatedBy;

			
			@Embedded
			private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();

	


}
