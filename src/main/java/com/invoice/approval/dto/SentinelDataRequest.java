package com.invoice.approval.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SentinelDataRequest {
    
    @JsonProperty("fileName")
    private String fileName;
    
    @JsonProperty("data")
    private Object data;
    
    @JsonProperty("referenceId")
    private String referenceId;
    
    @JsonProperty("organizationId")
    private String organizationId;
    
    @JsonProperty("organizationBranchCode")
    private String organizationBranchCode;
}