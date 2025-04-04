package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeaderDetailDTO {

	private Long id;

	private String category;

	private String description;

	private int rate;

	private int qty;

	private int amount;

}
