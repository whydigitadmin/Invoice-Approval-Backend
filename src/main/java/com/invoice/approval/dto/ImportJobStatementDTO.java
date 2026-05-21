package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobStatementDTO {
    private String statementType;
    private String statementCode;
    private String statementText;
    private String isMandatory;
}