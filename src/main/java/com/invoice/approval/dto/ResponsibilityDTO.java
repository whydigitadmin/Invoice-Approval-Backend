package com.invoice.approval.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsibilityDTO {
	
	private Long id;
	private String responsibility;
	private List<ScreensDTO>screensDTO;
	private String createdBy;
	private boolean active;
	

}
