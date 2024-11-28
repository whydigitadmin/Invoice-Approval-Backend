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
@Table(name="roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolesgen")
	@SequenceGenerator(name = "rolesgen", sequenceName = "rolesseq", initialValue = 1000000001, allocationSize = 1)
	@Column(name = "rolesid")
	private Long id;
	@Column(name = "role")
	private String role;
	@Column(name = "createdby")
	private String createdBy;
	@Column(name = "modifiedby")
	private String updatedBy;
	
	
	private boolean active;
	
	@Embedded
	private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();
	
	@JsonManagedReference
	@OneToMany(mappedBy = "rolesVO", cascade = CascadeType.ALL)
	private List<RolesResponsibilityVO> rolesReposibilitiesVO;

	@JsonGetter("active")
    public String getActive() {
        return active ? "Active" : "In-Active";
    }
	

}
