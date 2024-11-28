package com.invoice.approval.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="screens")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScreensVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "screensgen")
	@SequenceGenerator(name = "screensgen", sequenceName = "screensseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "screensid")
	private Long id;
	
	@Column(name="screenname")
	private String screenName;
	
	@Column(name="orgid")
	private Long orgId;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "responsibilityid")
	private ResponsibilityVO responsibilityVO;

}
