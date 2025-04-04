package com.invoice.approval.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeaderDTO {

	private Long id;

	private String createdBy;

	private String DocId;

	private String docDt;

	private List<HeaderDetailDTO> headerDetailDto;

}
