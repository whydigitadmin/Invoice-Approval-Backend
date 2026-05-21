package com.invoice.approval.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SentinelProcessingDTO {
    private Long rawJsonId;
    private String insertedFlag;
    private String dataType;
    private String fileName;
    private String sentinelValue;
    private String referenceId;
    private String jobNo;           // ADD THIS - for job number
    private Long jobId;
    private String errorMessage;
    private LocalDateTime receivedTimestamp;
    private LocalDateTime processedTimestamp;
    
    public boolean isSuccess() {
        return "T".equals(insertedFlag);
    }
    
    public boolean isFailed() {
        return "F".equals(insertedFlag);
    }
}