// WhatsupApprovalLinkDTO.java (Lombok version)
package com.invoice.approval.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsupApprovalLinkDTO {
    private String url;
    private Instant expiresAt;
}