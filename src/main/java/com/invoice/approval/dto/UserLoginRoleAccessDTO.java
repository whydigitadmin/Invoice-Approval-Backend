package com.invoice.approval.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRoleAccessDTO {
	private String role;
	private Long roleId;
	private LocalDate startDate;
	private LocalDate endDate;
}
