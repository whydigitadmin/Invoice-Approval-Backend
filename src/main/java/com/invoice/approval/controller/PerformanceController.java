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
import com.invoice.approval.dto.PerformanceGoalsDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.POVO;
import com.invoice.approval.entity.PerformanceGoalsDtlVO;
import com.invoice.approval.entity.PerformanceGoalsVO;
import com.invoice.approval.repo.PerformanceGoalsDtlRepo;
import com.invoice.approval.service.PerformanceGoalsServices;

@RestController
@RequestMapping("/api/performancegoals")
public class PerformanceController extends BaseController {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeExpensesController.class);

	
	@Autowired
	PerformanceGoalsServices performanceGoalsService;
	
	@Autowired
	PerformanceGoalsDtlRepo performanceGoalsDtlRepo;
	
	
	@PutMapping("/createPerformanceGoal")
	public ResponseEntity<ResponseDTO> createPerformanceGoal(@RequestBody PerformanceGoalsDTO preGoalsDTO) {
		String methodName = "createPerformanceGoals()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> performanceGoalsVO = performanceGoalsService.createUpdatePerformanceGoals(preGoalsDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, performanceGoalsVO.get("message"));
			responseObjectsMap.put("performanceGoalsVO", performanceGoalsVO.get("preGoalsVO"));
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getReportingUserName")
	public ResponseEntity<ResponseDTO> getReportingUserName(@RequestParam String username) {
	    String methodName = "getReportingUserName()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO = null;
	    List<Map<String, Object>> getReportingUserName= null;
	    try {
	        // Don't cast! Just receive as Object or correct type
	    	getReportingUserName = performanceGoalsService.getReportingUserName(username);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Reporting Person Details found Successfully");
	        responseObjectsMap.put("getReportingUserName", getReportingUserName);
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

	
	@GetMapping("/getAll")
	public ResponseEntity<ResponseDTO> getAll( ) {
		String methodName = "getAll()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<PerformanceGoalsVO> performanceVO = new ArrayList<>();
		try {
			performanceVO = performanceGoalsService.findAll();
			responseObjectsMap.put("performanceVO", performanceVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getPerformanceGoalsbyreportingto")
	public ResponseEntity<ResponseDTO> getPerformanceGoalsbyreportingto(@RequestParam String reportingto) {
	    String methodName = "getPerformanceGoalsbyreportingto()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO = null;
	    List<Map<String, Object>> getPerformanceGoalsbyreportingto= null;
	    try {
	        // Don't cast! Just receive as Object or correct type
	    	getPerformanceGoalsbyreportingto = performanceGoalsService.getPerformanceGoalsbyreportingto(reportingto);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pre Goals Details found Successfully");
	        responseObjectsMap.put("getPerformanceGoalsbyreportingto", getPerformanceGoalsbyreportingto);
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

	
	
	@GetMapping("/getPerformanceGoals")
	public ResponseEntity<ResponseDTO> getPerformanceGoals(@RequestParam String userName) {
	    String methodName = "getPerformanceGoals()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO = null;
	    List<Map<String, Object>> getPerformanceGoals= null;
	    try {
	        // Don't cast! Just receive as Object or correct type
	    	 getPerformanceGoals = performanceGoalsService.getPerformanceGoalsbyUserName(userName);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pre Goals Details found Successfully");
	        responseObjectsMap.put("getPerformanceGoals", getPerformanceGoals);
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

	
	
	@GetMapping("/getPerformanceGoalsVOListById")
	public ResponseEntity<ResponseDTO> getPerformanceGoalsVOListById(@RequestParam Long id) {
	    String methodName = "getPerformanceGoalsVOListById()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO = null;
	    List<Map<String, Object>> getPerformanceGoalsDetails= null;
	    try {
	        // Don't cast! Just receive as Object or correct type
	    	getPerformanceGoalsDetails = performanceGoalsService.getPerformanceGoalsVOListById(id);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pre Goals Details found Successfully");
	        responseObjectsMap.put("getPerformanceGoalsDetails", getPerformanceGoalsDetails);
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

	
	
	@GetMapping("/getPerformanceGoalsDetails")
	public ResponseEntity<ResponseDTO> getPerformanceGoalsDetails(@RequestParam Long id) {
	    String methodName = "getPerformanceGoals()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO = null;
	    List<Map<String, Object>> getPerformanceGoalsDetails= null;
	    try {
	        // Don't cast! Just receive as Object or correct type
	    	getPerformanceGoalsDetails = performanceGoalsService.getPerformanceGoalsDtlbyid(id);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pre Goals Details found Successfully");
	        responseObjectsMap.put("getPerformanceGoalsDetails", getPerformanceGoalsDetails);
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

	
	
	
	
	@PutMapping("/updatePerformanceGoalsApprovedDetails")
	public ResponseEntity<ResponseDTO> updatePerformanceGoalsApprovedDetails(
	        @RequestParam(required = false) Long id,
	        @RequestParam(required = false) String approve1,
	        @RequestParam(required = false) String approve1name
	        ) {
	    
	    String methodName = "updatePerformanceGoalsApprovedDetails()";
	    LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	    String errorMsg = null;
	    Map<String, Object> responseObjectsMap = new HashMap<>();
	    ResponseDTO responseDTO;

	    try {
	        // Assuming this updates the ticket status internally
	    	PerformanceGoalsVO preformanceGoalsVO = performanceGoalsService.updatePerformanceGoalsApprovedDetails(id, approve1,approve1name);

	        responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "PreGoalsVO  updated successfully");
	        responseObjectsMap.put("preformanceGoalsVO", preformanceGoalsVO);
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
