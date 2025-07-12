package com.invoice.approval.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
	
	private Long id;
	private String title;
	private String description;
	private String createdBy;
	
	private String status;
	private String assignTo;
	private String solvedOn;
	private String solvedBy;
	private String userNote;
	private String adminNote;
	

}
