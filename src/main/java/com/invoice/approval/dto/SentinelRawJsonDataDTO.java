package com.invoice.approval.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SentinelRawJsonDataDTO {
    private String status;
    private Long rawJsonId;
    private Long jobId;
    private String insertedFlag;
    private String sentinelValue;
    private String message;
    private String error;
    private LocalDateTime processedTimestamp;
}