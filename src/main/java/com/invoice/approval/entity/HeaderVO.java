package com.invoice.approval.entity;

import java.math.BigDecimal;
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
@Table(name = "GST_HEADER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeaderVO {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "headergen")
	@SequenceGenerator(name = "headergenseq", sequenceName = "headergenseq", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "GST_HEADERID")
    private Long id;
	

	  
	  
	   @Column(name = "cancel", length = 1)
	    private String cancel="F";

	    @Column(name = "createdby", length = 30)
	    private String createdBy;

	    @Column(name = "modifiedby", length = 30)
	    private String modifiedBy;
	    
	    @Column(name = "docid", length = 30)
	    private String DocId;
	    
	    @Column(name = "docdt", length = 30)
	    private String docDt;
	    
	    @OneToMany(mappedBy ="header",cascade = CascadeType.ALL)
	    @JsonManagedReference
	    private List<HeaderDetailVO> headerDetail;

	    @Column(name = "total")
	    private int total;
	    
		@Embedded
		private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();
}
