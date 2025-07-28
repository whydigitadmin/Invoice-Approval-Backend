package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PODTLDTO {

	private Long id;

	private String item;

	private String description;
	
	private String basecurr;

	private float exrate;

	private int qty;
	
	private float rate;
	
	private float price;

	private float lcamount;

}
