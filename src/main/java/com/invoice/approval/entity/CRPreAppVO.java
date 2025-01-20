package com.invoice.approval.entity;

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
@Table(name = "GST_PRECREDIT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CRPreAppVO {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crepreappgen")
	@SequenceGenerator(name = "crepreappgen", sequenceName = "crepreappseq", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "GST_PRECREDITID")
    private Long id;
	

	  @Column(name = "branchname", length = 50)
	    private String branchName;
	  
	   @Column(name = "cancel", length = 1)
	    private String cancel="F";

	    @Column(name = "profoma", length = 30)
	    private String profoma;

	    @Column(name = "ptype", length = 10)
	    private String ptype;

	    @Column(name = "crremarks", length = 100)
	    private String crRemarks;

	    @Column(name = "partyname", length = 200)
	    private String partyName;

	    @Column(name = "partycode", length = 20)
	    private String partyCode;

	    @Column(name = "vchno", length = 30)
	    private String vchNo;

	    @Column(name = "vchdt")
	    private LocalDate vchDt;

	    @Column(name = "invamt", precision = 15, scale = 2)
	    private Double invAmt;

	    @Column(name = "cramt", precision = 15, scale = 2)
	    private Double crAmt;

	    @Column(name = "reason", length = 200)
	    private String reason;
	    
		@Column(name = "approve1", length = 1)
	    private String approve1="F";	
		

	    @Column(name = "APPROVE1NAME", length = 30)
	    private String approve1Name;

	    @Column(name = "APPROVE1ON")
	    private LocalDateTime approve1On;
	
		@Column(name = "createdby")
		private String createdBy;
		@Column(name = "modifiedby")
		private String updatedBy;

		
		@Embedded
		private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();

}
