// WhatsAppApprovalRequestDTO.java (Lombok version)
package com.invoice.approval.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppApprovalRequestDTO {
    @NotBlank
    private String number;
    
    @NotBlank
    private String userType;
    
    @NotBlank
    private String userName;
    
    private Long invoiceId;
    private String action;
}