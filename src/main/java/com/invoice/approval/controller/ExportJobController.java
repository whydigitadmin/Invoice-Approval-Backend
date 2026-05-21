package com.invoice.approval.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.ExportJobRequestDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.service.ExportJobService;
import com.invoice.approval.service.SentinelJwtService;

@CrossOrigin
@RestController
@RequestMapping("/api/exportjob")
public class ExportJobController extends BaseController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ExportJobController.class);

    @Autowired
    private ExportJobService exportJobService;
    
    @Autowired
    private SentinelJwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createExportJob(
            @RequestBody ExportJobRequestDTO requestDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String methodName = "createExportJob()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        String errorMsg = null;
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO = null;
        
        // ========== JWT TOKEN VALIDATION ==========
        // Validate JWT token for PUT request (data modification)
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
        LOGGER.info("JWT token validated successfully for export job creation");
        // ========== END TOKEN VALIDATION ==========
        
        try {
            Map<String, Object> result = exportJobService.createExportJob(requestDTO);
            responseObjectsMap.put(CommonConstant.STRING_MESSAGE, result.get("message"));
            responseObjectsMap.put("exportJob", result.get("exportJob"));
            responseDTO = createServiceResponse(responseObjectsMap);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
        }
        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
        return ResponseEntity.ok().body(responseDTO);
    }

//    @GetMapping("/getAll")
//    public ResponseEntity<ResponseDTO> getAllExportJobs() {
//        String methodName = "getAllExportJobs()";
//        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//        String errorMsg = null;
//        Map<String, Object> responseObjectsMap = new HashMap<>();
//        ResponseDTO responseDTO = null;
//        List<UTExportJob> exportJobs = new ArrayList<>();
//        try {
//            exportJobs = exportJobService.getAllExportJobs();
//            responseObjectsMap.put("exportJobs", exportJobs);
//            responseDTO = createServiceResponse(responseObjectsMap);
//        } catch (Exception e) {
//            errorMsg = e.getMessage();
//            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
//            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
//        }
//        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//        return ResponseEntity.ok().body(responseDTO);
//    }

//    @GetMapping("/getById")
//    public ResponseEntity<ResponseDTO> getExportJobById(@RequestParam Long id) {
//        String methodName = "getExportJobById()";
//        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//        String errorMsg = null;
//        Map<String, Object> responseObjectsMap = new HashMap<>();
//        ResponseDTO responseDTO = null;
//        try {
//            UTExportJob exportJob = exportJobService.getExportJobById(id);
//            responseObjectsMap.put("exportJob", exportJob);
//            responseDTO = createServiceResponse(responseObjectsMap);
//        } catch (Exception e) {
//            errorMsg = e.getMessage();
//            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
//            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
//        }
//        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//        return ResponseEntity.ok().body(responseDTO);
//    }

//    @GetMapping("/getByReferenceId")
//    public ResponseEntity<ResponseDTO> getExportJobByReferenceId(@RequestParam String referenceId) {
//        String methodName = "getExportJobByReferenceId()";
//        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//        String errorMsg = null;
//        Map<String, Object> responseObjectsMap = new HashMap<>();
//        ResponseDTO responseDTO = null;
//        try {
//            UTExportJob exportJob = exportJobService.getExportJobByReferenceId(referenceId);
//            responseObjectsMap.put("exportJob", exportJob);
//            responseDTO = createServiceResponse(responseObjectsMap);
//        } catch (Exception e) {
//            errorMsg = e.getMessage();
//            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
//            responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
//        }
//        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//        return ResponseEntity.ok().body(responseDTO);
//    }
}