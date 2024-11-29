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
@Table(name="rolesresponsibility")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesResponsibilityVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolesresponsibilitygen")
	@SequenceGenerator(name = "rolesresponsibilitygen", sequenceName = "rolesresponsibilityseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "rolesresponsibilityid")
	private Long id;
	
	@Column(name="responsibilityid")
	private Long responsibilityId;
	
	@Column(name="responsibility",length = 50)
	private String responsibility;
	
	
	
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "rolesid")
	private RolesVO rolesVO;

}
