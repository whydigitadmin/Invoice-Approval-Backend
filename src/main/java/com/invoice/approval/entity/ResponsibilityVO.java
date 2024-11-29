package com.invoice.approval.entity;

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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="responsibility")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponsibilityVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "responsibilitygen")
	@SequenceGenerator(name = "responsibilitygen", sequenceName = "responsibilityseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "responsibilityid")
	private Long id;
	
	@Column(name="responsibility",length = 50)
	private String responsibility;
	
	@Column(name="createdby",length = 50)
	private String createdBy;
	
	@Column(name="modifiedby",length = 50)
	private String updatedBy;
	
	private boolean active;

	@JsonManagedReference
	@OneToMany(mappedBy = "responsibilityVO", cascade = CascadeType.ALL)
	private List<ScreensVO>screensVO;
	
	@Embedded
	private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();

	@JsonGetter("active")
    public String getActive() {
        return active ? "Active" : "In-Active";
    }
	

}