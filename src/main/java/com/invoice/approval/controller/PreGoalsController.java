package com.invoice.approval.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.PreGoalsDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.PreGoalsDtlVO;
import com.invoice.approval.entity.PreGoalsVO;
import com.invoice.approval.repo.PreGoalsDtlRepo;
import com.invoice.approval.service.PreGoalsServices;

@RestController
@RequestMapping("/api/pregoals")
public class PreGoalsController extends BaseController {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeExpensesController.class);

	
	@Autowired
	PreGoalsServices preGoalsService;
	
	@Autowired
	PreGoalsDtlRepo preGoalsDtlRepo;
	
	
	@PutMapping("/createPreGoal")
	public ResponseEntity<ResponseDTO> createPreGoal(@RequestBody PreGoalsDTO preGoalsDTO) {
		String methodName = "createPreGoals()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> preGoalsVO = preGoalsService.createUpdatePreGoals(preGoalsDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, preGoalsVO.get("message"));
			responseObjectsMap.put("preGoalsVO", preGoalsVO.get("preGoalsVO"));
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getPreGoalsbyreportingto")
	public ResponseEntity<ResponseDTO> getPreGoalsbyreportingto(@RequestParam String reportingto) {
	    String methodName = "getPreGoalsbyreportingto()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO = null;
	    List<Map<String, Object>> getPreGoalsbyreportingto= null;
	    try {
	        // Don't cast! Just receive as Object or correct type
	    	getPreGoalsbyreportingto = preGoalsService.getPreGoalsbyreportingto(reportingto);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pre Goals Details found Successfully");
	        responseObjectsMap.put("getPreGoalsbyreportingto", getPreGoalsbyreportingto);
	        responseDTO = createServiceResponse(responseObjectsMap);
	    } catch (Exception e) {
	        errorMsg = e.getMessage();
	        LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
	        responseDTO = createServiceResponseError(responseObjectsMap,
	                "Pre Goals Details information receive failed", errorMsg);
	    }

	    LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	    return ResponseEntity.ok().body(responseDTO);
	}

	
	
	@GetMapping("/getPreGoals")
	public ResponseEntity<ResponseDTO> getPreGoals(@RequestParam String userName) {
	    String methodName = "getPreGoals()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO = null;
	    List<Map<String, Object>> getPreGoals= null;
	    try {
	        // Don't cast! Just receive as Object or correct type
	    	 getPreGoals = preGoalsService.getPreGoalsbyUserName(userName);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pre Goals Details found Successfully");
	        responseObjectsMap.put("getPreGoals", getPreGoals);
	        responseDTO = createServiceResponse(responseObjectsMap);
	    } catch (Exception e) {
	        errorMsg = e.getMessage();
	        LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
	        responseDTO = createServiceResponseError(responseObjectsMap,
	                "Pre Goals Details information receive failed", errorMsg);
	    }

	    LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	    return ResponseEntity.ok().body(responseDTO);
	}

	
	
	@GetMapping("/getPreGoalsVOListById")
	public ResponseEntity<ResponseDTO> getPreGoalsVOListById(@RequestParam Long id) {
	    String methodName = "getPreGoalsVOListById()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO = null;
	    List<Map<String, Object>> getPreGoalsDetails= null;
	    try {
	        // Don't cast! Just receive as Object or correct type
	    	getPreGoalsDetails = preGoalsService.getPreGoalsVOListById(id);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pre Goals Details found Successfully");
	        responseObjectsMap.put("getPreGoalsDetails", getPreGoalsDetails);
	        responseDTO = createServiceResponse(responseObjectsMap);
	    } catch (Exception e) {
	        errorMsg = e.getMessage();
	        LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
	        responseDTO = createServiceResponseError(responseObjectsMap,
	                "Pre Goals Details information receive failed", errorMsg);
	    }

	    LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	    return ResponseEntity.ok().body(responseDTO);
	}

	
	
	@GetMapping("/getPreGoalsDetails")
	public ResponseEntity<ResponseDTO> getPreGoalsDetails(@RequestParam Long id) {
	    String methodName = "getPreGoals()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO = null;
	    List<Map<String, Object>> getPreGoalsDetails= null;
	    try {
	        // Don't cast! Just receive as Object or correct type
	    	getPreGoalsDetails = preGoalsService.getPreGoalsDtlbyid(id);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pre Goals Details found Successfully");
	        responseObjectsMap.put("getPreGoalsDetails", getPreGoalsDetails);
	        responseDTO = createServiceResponse(responseObjectsMap);
	    } catch (Exception e) {
	        errorMsg = e.getMessage();
	        LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
	        responseDTO = createServiceResponseError(responseObjectsMap,
	                "Pre Goals Details information receive failed", errorMsg);
	    }

	    LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	    return ResponseEntity.ok().body(responseDTO);
	}

	
	
	
	
	@PutMapping("/updatePreGoalsApprovedDetails")
	public ResponseEntity<ResponseDTO> updatePreGoalsApprovedDetails(
	        @RequestParam(required = false) Long id,
	        @RequestParam(required = false) String approve1,
	        @RequestParam(required = false) String approve1name
	        ) {
	    
	    String methodName = "updatePreGoalsApprovedDetails()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO;

	    try {
	        // Assuming this updates the ticket status internally
	        PreGoalsVO preGoalsVO = preGoalsService.updatePreGoalsApprovedDetails(id, approve1,approve1name);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "PreGoalsVO  updated successfully");
	        responseObjectsMap.put("preGoalsVO", preGoalsVO);
	        responseDTO = createServiceResponse(responseObjectsMap);
	    } catch (Exception e) {
	        errorMsg = e.getMessage();
	        LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
	        responseDTO = createServiceResponseError(responseObjectsMap, "PreGoalsVO  update failed", errorMsg);
	    }

	    LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	    return ResponseEntity.ok().body(responseDTO);
	}
	
	
}
