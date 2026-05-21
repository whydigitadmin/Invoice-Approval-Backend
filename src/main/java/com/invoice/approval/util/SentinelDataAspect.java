package com.invoice.approval.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoice.approval.entity.SentinelRawJsonData;
import com.invoice.approval.repo.SentinelJsonRepo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class SentinelDataAspect {

    @Autowired
    private SentinelJsonRepo sentinelJsonRepo;
    
    @Autowired
    private SentinelParser sentinelParser;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Around("execution(* com.invoice.approval.controller.ImportJobController.createImportJob(..))")
    public Object captureImportJson(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("=== Capturing IMPORT Job ===");
        
        Object[] args = joinPoint.getArgs();
        Object requestDTO = args.length > 0 ? args[0] : null;
        
        String referenceId = null;
        String jobNo = null;
        String organizationId = null;
        
        if (requestDTO != null) {
            try {
                // Extract values from headerDetails (CORRECT LOCATION)
                referenceId = extractValueFromPath(requestDTO, "headerDetails", "referenceId");
                jobNo = extractValueFromPath(requestDTO, "headerDetails", "jobNo");
                organizationId = extractValueFromPath(requestDTO, "headerDetails", "organizationId");
                
                // CRITICAL FIX: Handle empty referenceId - use jobNo as fallback
                if (referenceId == null || referenceId.trim().isEmpty()) {
                    referenceId = jobNo;
                    log.warn("referenceId is empty/null, using jobNo as referenceId: {}", referenceId);
                }
                
                log.info("Extracted IMPORT values - JobNo: {}, ReferenceId: {}, OrganizationId: {}", 
                    jobNo, referenceId, organizationId);
                
            } catch (Exception e) {
                log.warn("Could not extract import values: {}", e.getMessage(), e);
            }
        }
        
        // Execute the original method
        Object result = joinPoint.proceed();
        
        // Save to Sentinel table
        if (requestDTO != null && jobNo != null) {
            saveToSentinel(requestDTO, "IMPORT", referenceId, jobNo, organizationId);
        } else {
            log.error("Cannot save to Sentinel - jobNo is null");
        }
        
        return result;
    }
    
    @Around("execution(* com.invoice.approval.controller.ExportJobController.createExportJob(..))")
    public Object captureExportJson(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("=== Capturing EXPORT Job ===");
        
        Object[] args = joinPoint.getArgs();
        Object requestDTO = args.length > 0 ? args[0] : null;
        
        String referenceId = null;
        String jobNo = null;
        String organizationId = null;
        
        if (requestDTO != null) {
            try {
                // Extract values from headerDetails
                referenceId = extractValueFromPath(requestDTO, "headerDetails", "referenceId");
                jobNo = extractValueFromPath(requestDTO, "headerDetails", "jobNo");
                organizationId = extractValueFromPath(requestDTO, "headerDetails", "organizationId");
                
                // CRITICAL FIX: Handle empty referenceId - use jobNo as fallback
                if (referenceId == null || referenceId.trim().isEmpty()) {
                    referenceId = jobNo;
                    log.warn("referenceId is empty/null, using jobNo as referenceId: {}", referenceId);
                    
                    // Update the DTO to have a valid referenceId
                    updateReferenceIdInDTO(requestDTO, referenceId);
                }
                
                log.info("Extracted EXPORT values - JobNo: {}, ReferenceId: {}, OrganizationId: {}", 
                    jobNo, referenceId, organizationId);
                
            } catch (Exception e) {
                log.warn("Could not extract export values: {}", e.getMessage(), e);
            }
        }
        
        // Execute the original method
        Object result = joinPoint.proceed();
        
        // Save to Sentinel table
        if (requestDTO != null && jobNo != null) {
            saveToSentinel(requestDTO, "EXPORT", referenceId, jobNo, organizationId);
        }
        
        return result;
    }

    // Helper method to update referenceId in DTO
    private void updateReferenceIdInDTO(Object requestDTO, String newReferenceId) {
        try {
            Object job = getNestedObject(requestDTO, "getJob");
            if (job != null) {
                Object headerDetails = getNestedObject(job, "getHeaderDetails");
                if (headerDetails != null) {
                    java.lang.reflect.Method setter = headerDetails.getClass().getMethod("setReferenceId", String.class);
                    setter.invoke(headerDetails, newReferenceId);
                    log.info("Updated DTO referenceId to: {}", newReferenceId);
                }
            }
        } catch (Exception e) {
            log.warn("Could not update referenceId in DTO: {}", e.getMessage());
        }
    }
    /**
     * Generic method to extract value from a path
     * Example: extractValueFromPath(requestDTO, "headerDetails", "referenceId")
     */
    private String extractValueFromPath(Object requestDTO, String container, String field) {
        try {
            Object job = getNestedObject(requestDTO, "getJob");
            if (job == null) return null;
            
            String containerGetter = "get" + container.substring(0, 1).toUpperCase() + container.substring(1);
            Object containerObj = getNestedObject(job, containerGetter);
            if (containerObj == null) return null;
            
            String fieldGetter = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
            return getStringValue(containerObj, fieldGetter);
            
        } catch (Exception e) {
            log.debug("Error extracting {}.{}: {}", container, field, e.getMessage());
            return null;
        }
    }
    
    /**
     * Helper method to get nested object using reflection
     */
    private Object getNestedObject(Object obj, String methodName) {
        try {
            java.lang.reflect.Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Helper method to get string value using reflection
     */
    private String getStringValue(Object obj, String methodName) {
        try {
            java.lang.reflect.Method method = obj.getClass().getMethod(methodName);
            Object value = method.invoke(obj);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Save to Sentinel table with proper values
     */
    private void saveToSentinel(Object requestDTO, String dataType, String referenceId, String jobNo, String organizationId) {
        try {
            // Generate filename using jobNo or referenceId
            String fileIdentifier = (referenceId != null && !referenceId.isEmpty()) ? referenceId : jobNo;
            String fileName = dataType + "_" + fileIdentifier + ".json";
            String sentinelValue = sentinelParser.extractSentinelFromFilename(fileName);
            String jsonPayload = objectMapper.writeValueAsString(requestDTO);
            
            // Check if record already exists for this jobNo
            java.util.Optional<SentinelRawJsonData> existing = sentinelJsonRepo.findByJobNo(jobNo);
            
            SentinelRawJsonData rawData;
            if (existing.isPresent()) {
                rawData = existing.get();
                log.info("Updating existing sentinel record for jobNo: {}", jobNo);
                rawData.setJsonPayload(jsonPayload);
                rawData.setFileName(fileName);
                rawData.setSentinelValue(sentinelValue);
            } else {
                rawData = new SentinelRawJsonData();
                log.info("Creating new sentinel record for jobNo: {}", jobNo);
            }
            
            // Set all fields - CRITICAL: Set both JOB_NO and JOB_ID to jobNo
            rawData.setJsonPayload(jsonPayload);
            rawData.setDataType(dataType);
            rawData.setFileName(fileName);
            rawData.setSentinelValue(sentinelValue);
            rawData.setReferenceId(referenceId);  // Store referenceId (or jobNo if empty)
            rawData.setJobNo(jobNo);              // Store jobNo in JOB_NO column
            rawData.setJobId(jobNo);              // Store jobNo in JOB_ID column as well
            rawData.setInsertedFlag("T");
            rawData.setReceivedTimestamp(LocalDateTime.now());
            rawData.setProcessedTimestamp(LocalDateTime.now());
            
            SentinelRawJsonData saved = sentinelJsonRepo.save(rawData);
            
            log.info("✅ Saved to Sentinel - ID: {}, Type: {}, JobNo: {}, ReferenceId: {}, JobId: {}", 
                    saved.getRawJsonId(), dataType, jobNo, referenceId, jobNo);
            
        } catch (Exception e) {
            log.error("Failed to save to Sentinel: {}", e.getMessage(), e);
        }
    }
}