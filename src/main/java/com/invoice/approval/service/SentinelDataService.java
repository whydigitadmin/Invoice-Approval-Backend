package com.invoice.approval.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoice.approval.dto.SentinelProcessingDTO;
import com.invoice.approval.entity.SentinelRawJsonData;
import com.invoice.approval.entity.UTExportJob;
import com.invoice.approval.entity.UTImportJob;
import com.invoice.approval.repo.SentinelJsonRepo;
import com.invoice.approval.repo.UTExportJobRepo;
import com.invoice.approval.repo.UTImportJobRepo;
import com.invoice.approval.util.SentinelParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SentinelDataService {

    @Autowired
    private SentinelJsonRepo rawJsonDataRepository;
    
    @Autowired
    private UTImportJobRepo importJobRepository;
    
    @Autowired
    private UTExportJobRepo exportJobRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private SentinelParser sentinelParser;
    
    @Transactional
    public void processIncomingData(String jsonData, String dataType, String fileName, String jobNo, String organizationId) {
        try {
            // Validate dataType
            if (!"IMPORT".equalsIgnoreCase(dataType) && !"EXPORT".equalsIgnoreCase(dataType)) {
                throw new IllegalArgumentException("dataType must be 'IMPORT' or 'EXPORT'");
            }
            
            // Convert to upper case for consistency
            String normalizedDataType = dataType.toUpperCase();
            
            // Extract sentinel from filename
            String sentinelValue = sentinelParser.extractSentinelFromFilename(fileName);
            
            // Parse JSON to extract header information
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode headerDetails = rootNode.path("job").path("headerDetails");
            
            // Extract jobNo from JSON if not provided
            String finalJobNo = jobNo;
            if (finalJobNo == null || finalJobNo.isEmpty()) {
                finalJobNo = headerDetails.path("jobNo").asText(null);
                if (finalJobNo == null || finalJobNo.isEmpty()) {
                    finalJobNo = UUID.randomUUID().toString();
                    log.warn("jobNo not found in JSON, generated: {}", finalJobNo);
                }
            }
            
            // Extract referenceId - ensure it's never null
            String referenceId = headerDetails.path("referenceId").asText(null);
            if (referenceId == null || referenceId.isEmpty()) {
                referenceId = finalJobNo;  // Use jobNo as fallback
                log.warn("referenceId not found or empty in JSON, using jobNo: {}", referenceId);
            }
            
            log.info("Processing {} data with jobNo: {}, referenceId: {}", normalizedDataType, finalJobNo, referenceId);
            
            // CHECK IF ALREADY PROCESSED - Prevent duplicates based on jobNo and dataType
            Optional<SentinelRawJsonData> existingRecord = rawJsonDataRepository.findByJobNoAndDataType(finalJobNo, normalizedDataType);
            if (existingRecord.isPresent()) {
                log.info("Record already exists for jobNo: {} and dataType: {}, updating existing record", finalJobNo, normalizedDataType);
                
                // Update existing record's json payload if needed
                SentinelRawJsonData existingData = existingRecord.get();
                existingData.setJsonPayload(jsonData);
                existingData.setFileName(fileName);
                existingData.setSentinelValue(sentinelValue);
                existingData.setJobNo(finalJobNo);
                existingData.setReferenceId(referenceId);
                existingData.setReceivedTimestamp(LocalDateTime.now());
                rawJsonDataRepository.save(existingData);
                log.info("Updated existing record with ID: {}", existingData.getRawJsonId());
                
                // Process the job update
                processJobData(jsonData, normalizedDataType, finalJobNo, referenceId, organizationId, existingData.getRawJsonId());
                return;
            }
            
            // 1. Store raw JSON as-is with filename and sentinel
            SentinelRawJsonData rawData = new SentinelRawJsonData();
            rawData.setJsonPayload(jsonData);
            rawData.setDataType(normalizedDataType);
            rawData.setFileName(fileName);
            rawData.setSentinelValue(sentinelValue);
            rawData.setJobNo(finalJobNo);
            rawData.setReferenceId(referenceId);
            rawData.setInsertedFlag("F");
            
            log.info("Saving raw data with jobNo: {}, referenceId: {}", finalJobNo, referenceId);
            
            SentinelRawJsonData savedRawData = rawJsonDataRepository.save(rawData);
            rawJsonDataRepository.flush();
            
            log.info("Saved raw data with ID: {}", savedRawData.getRawJsonId());
            
            // Process the job data
            processJobData(jsonData, normalizedDataType, finalJobNo, referenceId, organizationId, savedRawData.getRawJsonId());
            
        } catch (Exception e) {
            log.error("Error in processIncomingData: ", e);
            throw new RuntimeException("Failed to process incoming data: " + e.getMessage(), e);
        }
    }
    
    private void processJobData(String jsonData, String dataType, String jobNo, String referenceId, String organizationId, Long rawJsonId) {
        boolean insertSuccess = false;
        String jobIdValue = null;  // Changed from Long to String to store jobNo
        String errorMessage = null;
        
        try {
            if ("IMPORT".equals(dataType)) {
                // Check if import job already exists by jobNo
                Optional<UTImportJob> existingImportJob = importJobRepository.findByJobNo(jobNo);
                
                UTImportJob importJob;
                if (existingImportJob.isPresent()) {
                    importJob = existingImportJob.get();
                    log.info("Updating existing import job with jobNo: {}", jobNo);
                } else {
                    importJob = objectMapper.readValue(jsonData, UTImportJob.class);
                    log.info("Creating new import job with jobNo: {}", jobNo);
                }
                
                // Set fields
                importJob.setJobNo(jobNo);
                importJob.setReferenceId(referenceId);
                if (importJob.getOrganizationId() == null || importJob.getOrganizationId().isEmpty()) {
                    importJob.setOrganizationId(organizationId);
                }
                if (importJob.getTimeStamp() == null) {
                    importJob.setTimeStamp(LocalDateTime.now());
                }
                
                // Save to UT_IMPORTJOB table
                UTImportJob savedJob = importJobRepository.save(importJob);
                // IMPORTANT: Store jobNo in jobIdValue, not the database ID
                jobIdValue = savedJob.getJobNo();  // This is the business key (ICB/1/2026-27)
                insertSuccess = true;
                log.info("Import data saved - JobNo: {}, ReferenceId: {}", jobNo, referenceId);
                
            } else if ("EXPORT".equals(dataType)) {
                // Check if export job already exists by jobNo
                Optional<UTExportJob> existingExportJob = exportJobRepository.findByJobNo(jobNo);
                
                UTExportJob exportJob;
                if (existingExportJob.isPresent()) {
                    exportJob = existingExportJob.get();
                    log.info("Updating existing export job with jobNo: {}", jobNo);
                } else {
                    exportJob = objectMapper.readValue(jsonData, UTExportJob.class);
                    log.info("Creating new export job with jobNo: {}", jobNo);
                }
                
                // Set fields
                exportJob.setJobNo(jobNo);
                exportJob.setReferenceId(referenceId);
                if (exportJob.getOrganizationId() == null || exportJob.getOrganizationId().isEmpty()) {
                    exportJob.setOrganizationId(organizationId);
                }
                if (exportJob.getTimeStamp() == null) {
                    exportJob.setTimeStamp(Instant.now());
                }
                if (exportJob.getCreatedOn() == null) {
                    exportJob.setCreatedOn(LocalDateTime.now().toString());
                    exportJob.setCreatedBy("SYSTEM");
                }
                if (exportJob.getModifiedOn() == null) {
                    exportJob.setModifiedOn(LocalDateTime.now().toString());
                    exportJob.setModifiedBy("SYSTEM");
                }
                
                // Save to UT_EXPORTJOB table
                UTExportJob savedJob = exportJobRepository.save(exportJob);
                // IMPORTANT: Store jobNo in jobIdValue, not the database ID
                jobIdValue = savedJob.getJobNo();  // This is the business key (ECB/3/2026-27)
                insertSuccess = true;
                log.info("Export data saved - JobNo: {}, ReferenceId: {}", jobNo, referenceId);
            }
            
        } catch (Exception e) {
            log.error("Failed to insert into main table: {}", e.getMessage(), e);
            insertSuccess = false;
            errorMessage = e.getMessage();
        }
        
        // Update the sentinel record with jobNo in BOTH jobNo AND jobId columns
        if (insertSuccess && jobIdValue != null) {
            // Store the SAME jobNo value in both columns
            rawJsonDataRepository.updateSuccessWithJobNo(rawJsonId, jobIdValue, jobNo, referenceId);
            log.info("Sentinel record updated - ID: {}, JOB_ID: {}, JOB_NO: {}, REFERENCE_ID: {}", 
                rawJsonId, jobIdValue, jobNo, referenceId);
        } else {
            String finalErrorMessage = errorMessage != null ? errorMessage : "Failed to insert data into " + dataType + " table";
            rawJsonDataRepository.updateFailureWithDetails(rawJsonId, finalErrorMessage);
            log.error("Sentinel record marked as FAILED for ID: {}", rawJsonId);
        }
    }
    
    @Transactional
    public void processFileUpload(MultipartFile file, String dataType, String jobNo, String organizationId) throws IOException {
        String fileName = file.getOriginalFilename();
        String jsonData = new String(file.getBytes(), StandardCharsets.UTF_8);
        processIncomingData(jsonData, dataType, fileName, jobNo, organizationId);
    }
    
    @Transactional
    public void processFileUpload(MultipartFile file, String dataType) throws IOException {
        String fileName = file.getOriginalFilename();
        String jsonData = new String(file.getBytes(), StandardCharsets.UTF_8);
        String jobNo = null;
        String organizationId = null;
        
        // Try to extract jobNo from JSON
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode headerDetails = rootNode.path("job").path("headerDetails");
            jobNo = headerDetails.path("jobNo").asText(null);
            organizationId = headerDetails.path("organizationId").asText(null);
        } catch (Exception e) {
            log.warn("Could not extract jobNo from JSON");
        }
        
        processIncomingData(jsonData, dataType, fileName, jobNo, organizationId);
    }
    
    @Transactional
    public void processJsonData(String jsonData, String dataType, String fileName, String jobNo, String organizationId) {
        processIncomingData(jsonData, dataType, fileName, jobNo, organizationId);
    }
    
    @Transactional
    public void processJsonData(String jsonData, String dataType, String fileName) {
        String jobNo = null;
        String organizationId = null;
        
        // Try to extract jobNo from JSON
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode headerDetails = rootNode.path("job").path("headerDetails");
            jobNo = headerDetails.path("jobNo").asText(null);
            organizationId = headerDetails.path("organizationId").asText(null);
        } catch (Exception e) {
            log.warn("Could not extract jobNo from JSON");
        }
        
        processIncomingData(jsonData, dataType, fileName, jobNo, organizationId);
    }
    
    @Transactional(readOnly = true)
    public SentinelProcessingDTO getProcessingStatus(Long rawJsonId) {
        Optional<SentinelRawJsonData> optional = rawJsonDataRepository.findById(rawJsonId);
        if (optional.isPresent()) {
            SentinelRawJsonData data = optional.get();
            return SentinelProcessingDTO.builder()
                .rawJsonId(data.getRawJsonId())
                .insertedFlag(data.getInsertedFlag())
                .dataType(data.getDataType())
                .fileName(data.getFileName())
                .sentinelValue(data.getSentinelValue())
                .jobNo(data.getJobNo())
                .referenceId(data.getReferenceId())
                .jobId(data.getJobId() != null ? Long.valueOf(data.getJobId().toString()) : null)
                .errorMessage(data.getErrorMessage())
                .receivedTimestamp(data.getReceivedTimestamp())
                .processedTimestamp(data.getProcessedTimestamp())
                .build();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public SentinelProcessingDTO getProcessingStatusByJobNo(String jobNo) {
        Optional<SentinelRawJsonData> optional = rawJsonDataRepository.findByJobNo(jobNo);
        if (optional.isPresent()) {
            SentinelRawJsonData data = optional.get();
            return SentinelProcessingDTO.builder()
                .rawJsonId(data.getRawJsonId())
                .insertedFlag(data.getInsertedFlag())
                .dataType(data.getDataType())
                .fileName(data.getFileName())
                .sentinelValue(data.getSentinelValue())
                .jobNo(data.getJobNo())
                .referenceId(data.getReferenceId())
                .jobId(data.getJobId() != null ? Long.valueOf(data.getJobId().toString()) : null)
                .errorMessage(data.getErrorMessage())
                .receivedTimestamp(data.getReceivedTimestamp())
                .processedTimestamp(data.getProcessedTimestamp())
                .build();
        }
        return null;
    }
}