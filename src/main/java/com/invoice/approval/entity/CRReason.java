package com.invoice.approval.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GST_CNREASON")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CRReason {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cnreasongen")
	@SequenceGenerator(name = "cnreasonappgen", sequenceName = "cnreasonappseq", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "GST_CNREASONID")
    private Long id;
	
	@Column(name = "cancel", length = 1)
    private String cancel="F";
	
	@Column(name = "code", length = 2)
    private String code;
	
	@Column(name = "crreason", length = 100)
    private String crReason;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "plimpact", length = 10)
    private String plImpact;
    
    @Column(name = "documentsrequired", length = 200)
    private String documentsRequired;

    @Column(name = "rejectremarks", length = 200)
    private String rejectremarks;
}
