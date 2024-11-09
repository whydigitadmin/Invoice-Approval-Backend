package com.invoice.approval.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ResponseErrors implements Serializable {

	private static final long serialVersionUID = 1L;

	private String errorType;

	private String errorCode;

	private String shortMessage;

	private String longMessage;

	private String logMessage;

}
