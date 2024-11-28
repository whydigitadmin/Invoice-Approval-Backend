package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenNamesDTO {
	
	private Long id;
	private String screenName;
	private  String screenCode;
	private boolean active;
	private String createdBy;

}
