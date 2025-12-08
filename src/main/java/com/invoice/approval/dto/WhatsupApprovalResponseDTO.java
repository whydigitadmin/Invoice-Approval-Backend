// WhatsupApprovalResponseDTO.java (Lombok version)
package com.invoice.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsupApprovalResponseDTO {
    private String docId;
    private String action;
    private String partyName;
    private String amount;
    private String status;
}