package com.invoice.approval.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoice.approval.dto.SentinelDataRequest;
import com.invoice.approval.dto.SentinelProcessingDTO;
import com.invoice.approval.service.SentinelDataService;
import com.invoice.approval.util.SentinelParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sentinel-data")
public class SentinelDataController {

    @Autowired
    private SentinelDataService dataService;
    
    @Autowired
    private SentinelParser sentinelParser;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> processImport(@RequestBody SentinelDataRequest request) {
        try {
            String jsonData = objectMapper.writeValueAsString(request.getData());
            String referenceId = request.getReferenceId() != null ? request.getReferenceId() : UUID.randomUUID().toString();
            String organizationId = request.getOrganizationId();
            String fileName = request.getFileName() != null ? request.getFileName() : "unknown_import.json";
            
            dataService.processIncomingData(jsonData, "IMPORT", fileName, referenceId, organizationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Import request processed successfully");
            response.put("referenceId", referenceId);
            response.put("fileName", fileName);
            response.put("sentinel", sentinelParser.extractSentinelFromFilename(fileName));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing import request", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("insertedFlag", "F");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/export")
    public ResponseEntity<Map<String, Object>> processExport(@RequestBody SentinelDataRequest request) {
        try {
            String jsonData = objectMapper.writeValueAsString(request.getData());
            String referenceId = request.getReferenceId() != null ? request.getReferenceId() : UUID.randomUUID().toString();
            String organizationId = request.getOrganizationId();
            String fileName = request.getFileName() != null ? request.getFileName() : "unknown_export.json";
            
            dataService.processIncomingData(jsonData, "EXPORT", fileName, referenceId, organizationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Export request processed successfully");
            response.put("referenceId", referenceId);
            response.put("fileName", fileName);
            response.put("sentinel", sentinelParser.extractSentinelFromFilename(fileName));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing export request", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("insertedFlag", "F");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/upload/import")
    public ResponseEntity<Map<String, Object>> uploadImportFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "organizationId", required = false) String organizationId,
            @RequestParam(value = "referenceId", required = false) String referenceId) {
        
        try {
            String fileName = file.getOriginalFilename();
            String refId = referenceId != null ? referenceId : UUID.randomUUID().toString();
            
            dataService.processFileUpload(file, "IMPORT", refId, organizationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "File uploaded and processed successfully");
            response.put("fileName", fileName);
            response.put("referenceId", refId);
            response.put("sentinel", sentinelParser.extractSentinelFromFilename(fileName));
            response.put("hasValidSentinel", sentinelParser.hasValidSentinel(fileName));
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error reading file", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to read file: " + e.getMessage());
            errorResponse.put("insertedFlag", "F");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @PostMapping("/upload/export")
    public ResponseEntity<Map<String, Object>> uploadExportFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "organizationId", required = false) String organizationId,
            @RequestParam(value = "referenceId", required = false) String referenceId) {
        
        try {
            String fileName = file.getOriginalFilename();
            String refId = referenceId != null ? referenceId : UUID.randomUUID().toString();
            
            dataService.processFileUpload(file, "EXPORT", refId, organizationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "File uploaded and processed successfully");
            response.put("fileName", fileName);
            response.put("referenceId", refId);
            response.put("sentinel", sentinelParser.extractSentinelFromFilename(fileName));
            response.put("hasValidSentinel", sentinelParser.hasValidSentinel(fileName));
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error reading file", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "Failed to read file: " + e.getMessage());
            errorResponse.put("insertedFlag", "F");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/status/{rawJsonId}")
    public ResponseEntity<SentinelProcessingDTO> getStatus(@PathVariable Long rawJsonId) {
        SentinelProcessingDTO status = dataService.getProcessingStatus(rawJsonId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/status/reference/{referenceId}")
    public ResponseEntity<SentinelProcessingDTO> getStatusByReferenceId(@PathVariable String referenceId) {
        SentinelProcessingDTO status = dataService.getProcessingStatusByJobNo(referenceId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}