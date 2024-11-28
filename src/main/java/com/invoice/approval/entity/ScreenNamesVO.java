package com.invoice.approval.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "screenname")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenNamesVO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "screennamegen")
	@SequenceGenerator(name = "screennamegen", sequenceName = "screennameseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "screennameid")
	private Long id;

	@Column(name = "screenname", length = 25)
	private String screenName;

	@Column(name = "screencode", length = 10)
	private String screenCode;

	private boolean active;
	@Column(name = "createdby", length = 25)
	private String createdBy;
	@Column(name = "modifiedby", length = 25)
	private String updatedBy;
	
	@JsonGetter("active")
	public String getActive() {
		return active ? "Active" : "In-Active";
	}
	
	@Embedded
	private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();

}

