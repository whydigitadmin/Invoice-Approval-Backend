package com.invoice.approval.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ut_sentineljson")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentinelRawJsonData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rawJsonGen")
    @SequenceGenerator(name = "rawJsonGen", sequenceName = "SEQ_UT_RAW_JSON_DATA", allocationSize = 1)
    @Column(name = "RAW_JSON_ID")
    private Long rawJsonId;
    
    @Column(name = "JSON_PAYLOAD", columnDefinition = "CLOB", nullable = false)
    private String jsonPayload;
    
    @Column(name = "DATA_TYPE", length = 20, nullable = false)
    private String dataType;
    
    @Column(name = "FILE_NAME", length = 500)
    private String fileName;
    
    @Column(name = "SENTINEL_VALUE", length = 100)
    private String sentinelValue;
    
    @Column(name = "JOB_NO", length = 100)  // ADD THIS FIELD
    private String jobNo;
    
    @Column(name = "INSERTED_FLAG", length = 1)
    private String insertedFlag = "F";
    
    @Column(name = "REFERENCE_ID", length = 50)
    private String referenceId;
    
    @Column(name = "JOB_ID", length = 100)  // Changed to String to accommodate jobNo
    private String jobId;  // Changed from Long to String
    
    @Column(name = "ERROR_MESSAGE", columnDefinition = "CLOB")
    private String errorMessage;
    
    @Column(name = "RECEIVED_TIMESTAMP")
    private LocalDateTime receivedTimestamp;
    
    @Column(name = "PROCESSED_TIMESTAMP")
    private LocalDateTime processedTimestamp;
    
    @PrePersist
    protected void onCreate() {
        receivedTimestamp = LocalDateTime.now();
    }
}