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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name="GST_PRECREDITATTACHMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CRPreAppAttachmentVO {
	
	

	
		
		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "precreditdtlgen")
		@SequenceGenerator(name = "precreditdtlgen", sequenceName = "precreditdtlgseq", initialValue = 1000000001, allocationSize = 1)
		@Column(name = "GST_PRECREDITATTACHMENTid")
		private Long id;
		
		
		 			@Lob
			@Column(name = "attachment", columnDefinition="BLOB")
			private byte[] attachment;
		
		@JsonManagedReference
		@JsonIgnore
		@ManyToOne
		@JoinColumn(name = "GST_PRECREDITID")
		private CRPreAppVO CrPreAppVO;
		
		
		

	


}
