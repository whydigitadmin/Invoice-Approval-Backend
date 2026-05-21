package com.invoice.approval.controller;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.ImportJobRequestDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.UTImportJob;
import com.invoice.approval.service.ImportJobService;
import com.invoice.approval.service.SentinelJwtService;

//CORRECT - Add these
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/api/importjob")
public class ImportJobController extends BaseController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ImportJobController.class);

    @Autowired
    private ImportJobService importJobService;
    
    @Autowired
    private SentinelJwtService jwtService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> createImportJob(
            @Valid @RequestBody ImportJobRequestDTO requestDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String methodName = "createImportJob()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        String errorMsg = null;
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO = null;
        
        // ========== JWT TOKEN VALIDATION ==========
        // Validate JWT token for POST request (data modification)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            errorMsg = "Missing or invalid Authorization header. Expected format: Bearer <token>";
            LOGGER.error(errorMsg);
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
        }
        
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        if (!jwtService.validateToken(token)) {
            errorMsg = "Invalid or expired JWT token. Access denied.";
            LOGGER.error(errorMsg);
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
        }
        
        // Log token validation success
        LOGGER.info("JWT token validated successfully for import job creation");
        // ========== END TOKEN VALIDATION ==========
        
        if (requestDTO == null) {
            errorMsg = "Request body cannot be null";
            LOGGER.error(errorMsg);
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.badRequest().body(responseDTO);
        }
        
        try {
            if (requestDTO.getJob() != null && requestDTO.getJob().getHeaderDetails() != null) {
                LOGGER.info("Processing import job creation for reference: {}", 
                    requestDTO.getJob().getHeaderDetails().getReferenceId());
            }
            
            Map<String, Object> result = importJobService.createImportJob(requestDTO);
            responseObjectsMap.put(CommonConstant.STRING_MESSAGE, result.get("message"));
            responseObjectsMap.put("importJob", result.get("importJob"));
            responseDTO = createServiceResponse(responseObjectsMap);
            LOGGER.info("Import job created successfully");
        } catch (IllegalArgumentException e) {
            errorMsg = "Validation error: " + e.getMessage();
            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.badRequest().body(responseDTO);
        } catch (Exception e) {
            errorMsg = "Internal server error: " + e.getMessage();
            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
            e.printStackTrace();
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        
        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getAllImportJobs() {
        String methodName = "getAllImportJobs()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        String errorMsg = null;
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO = null;
        List<UTImportJob> importJobs = new ArrayList<>();
        
        try {
            importJobs = importJobService.getAllImportJobs();
            responseObjectsMap.put("importJobs", importJobs);
            responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Successfully retrieved " + importJobs.size() + " import jobs");
            responseDTO = createServiceResponse(responseObjectsMap);
            LOGGER.info("Retrieved {} import jobs", importJobs.size());
        } catch (Exception e) {
            errorMsg = "Failed to retrieve import jobs: " + e.getMessage();
            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
            e.printStackTrace();
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        
        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getImportJobById(@RequestParam Long id) {
        String methodName = "getImportJobById()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        String errorMsg = null;
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO = null;
        
        if (id == null || id <= 0) {
            errorMsg = "Invalid id: " + id;
            LOGGER.error(errorMsg);
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.badRequest().body(responseDTO);
        }
        
        try {
            UTImportJob importJob = importJobService.getImportJobById(id);
            if (importJob == null) {
                errorMsg = "Import job not found with id: " + id;
                LOGGER.warn(errorMsg);
                responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            
            responseObjectsMap.put("importJob", importJob);
            responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Successfully retrieved import job");
            responseDTO = createServiceResponse(responseObjectsMap);
            LOGGER.info("Retrieved import job with id: {}", id);
        } catch (Exception e) {
            errorMsg = "Failed to retrieve import job: " + e.getMessage();
            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
            e.printStackTrace();
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        
        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
        return ResponseEntity.ok().body(responseDTO);
    }
    

    
    @GetMapping("/generate-permanent-token")
    public ResponseEntity<Map<String, String>> generatePermanentToken() {
        Map<String, String> response = new HashMap<>();
        try {
        	String secret = "mySuperUniworldLogistics887765433X";  
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            
            // Set expiry to year 2126 (100 years from 2026)
            // 100 years = 100 * 365 * 24 * 60 * 60 * 1000 milliseconds
            Date farFuture = new Date(System.currentTimeMillis() + 100L * 365 * 24 * 60 * 60 * 1000);
            
            String token = Jwts.builder()
                    .setSubject("sentinel-integration")
                    .setIssuer("sentinel")
                    .claim("role", "INTEGRATION_PARTNER")
                    .claim("type", "permanent")
                    .setIssuedAt(new Date())
                    .setExpiration(farFuture)  // Expires in 100 years
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            
            response.put("token", token);
            response.put("message", "Permanent token (valid for 100 years)");
            response.put("expires_in", "100 years");
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleAllExceptions(Exception ex) {
        Map<String, Object> responseObjectsMap = new HashMap<>();
        String errorMsg = "Error: " + ex.getMessage();
        
        // Log the full stack trace
        LOGGER.error("Exception caught in controller: ", ex);
        
        // Log the cause
        if (ex.getCause() != null) {
            LOGGER.error("Cause: ", ex.getCause());
            errorMsg += " | Cause: " + ex.getCause().getMessage();
        }
        
        responseObjectsMap.put("errorMessage", errorMsg);
        responseObjectsMap.put("message", errorMsg);
        
        ResponseDTO responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }
    
    @GetMapping("/generate-current-token")
    public ResponseEntity<Map<String, String>> generateCurrentToken() {
        Map<String, String> response = new HashMap<>();
        try {
        	String secret = "mySuperUniworldLogistics887765433X";
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            
            // 100 years from now
            long hundredYearsInMillis = 100L * 365 * 24 * 60 * 60 * 1000;
            Date expiry = new Date(System.currentTimeMillis() + hundredYearsInMillis);
            
            String token = Jwts.builder()
                    .setSubject("sentinel-integration")
                    .setIssuer("sentinel")
                    .claim("role", "INTEGRATION_PARTNER")
                    .setIssuedAt(new Date())
                    .setExpiration(expiry)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            
            response.put("token", token);
            response.put("issued_at", new Date().toString());
            response.put("expires_at", expiry.toString());
            response.put("message", "Use: Bearer " + token);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "/getByReferenceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getImportJobByReferenceId(@RequestParam String referenceId) {
        String methodName = "getImportJobByReferenceId()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        String errorMsg = null;
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO = null;
        
        if (referenceId == null || referenceId.trim().isEmpty()) {
            errorMsg = "ReferenceId cannot be null or empty";
            LOGGER.error(errorMsg);
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.badRequest().body(responseDTO);
        }
        
        try {
            UTImportJob importJob = importJobService.getImportJobByReferenceId(referenceId);
            if (importJob == null) {
                errorMsg = "Import job not found with referenceId: " + referenceId;
                LOGGER.warn(errorMsg);
                responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            
            responseObjectsMap.put("importJob", importJob);
            responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Successfully retrieved import job");
            responseDTO = createServiceResponse(responseObjectsMap);
            LOGGER.info("Retrieved import job with referenceId: {}", referenceId);
        } catch (Exception e) {
            errorMsg = "Failed to retrieve import job: " + e.getMessage();
            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
            e.printStackTrace();
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
        
        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
        return ResponseEntity.ok().body(responseDTO);
    }
}