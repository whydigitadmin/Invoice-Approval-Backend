package com.invoice.approval.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesDTO {
	
	private Long id;
	private String role;
	private String createdBy;
	private boolean active;
	private List<RolesResponsibilityDTO> rolesResponsibilityDTO;

}
