package com.invoice.approval.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.CRPreAppDTO;
import com.invoice.approval.dto.PartyDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.entity.GstInvoiceHdrVO;
import com.invoice.approval.repo.CRPreAppRepo;
import com.invoice.approval.repo.PartyMasterRepo;
import com.invoice.approval.service.CRPreAppService;
import com.invoice.approval.service.PartyService;

@CrossOrigin
@RestController
@RequestMapping("/api/crpreapp")

public class CRPreAppController extends BaseController {

	
	public static final Logger LOGGER = LoggerFactory.getLogger(PartyController.class);

	@Autowired
	CRPreAppService crePreAppService;
	
	@Autowired
	CRPreAppRepo crPreAppRepo; 
	

	@PutMapping("/updateCRPreApp")
	public ResponseEntity<ResponseDTO> updateCreateParty(@RequestBody CRPreAppDTO crPreAppDTO) {
		String methodName = "updateCreateCRPreApp()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;

		try {
			Map<String, Object> crPreAppVO = crePreAppService.updateCreateCRPreApp(crPreAppDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, crPreAppVO.get("message"));
			responseObjectsMap.put("crPreAppVO", crPreAppVO.get("crPreAppVO")); // Corrected key
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	@GetMapping("/getCRPendingDetails")
	public ResponseEntity<ResponseDTO> getPendingDetails(@RequestParam String userType,@RequestParam String userName) {
		String methodName = "getPendingDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> pendingApprovalDetails = new ArrayList<Map<String, Object>>();
		try {
			pendingApprovalDetails = crePreAppService.getPendingApprovalReport(userType,userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pending Approval Details  found Successfullly");
			responseObjectsMap.put("pendingApprovalDetails", pendingApprovalDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Pending Approval Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getCRDetailsApprove1")
	public ResponseEntity<ResponseDTO> getInvDetailsApprove1(@RequestParam String userType,@RequestParam String userName) {
		String methodName = "getInvDetailsApprove1()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> approvedApprovalDetails1 = new ArrayList<Map<String, Object>>();
		try {
			approvedApprovalDetails1 = crePreAppService.getApprovalReport(userType,userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Approved Approval1 Details  found Successfullly");
			responseObjectsMap.put("approvedApprovalDetails1", approvedApprovalDetails1);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Approved Approval1 Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@PutMapping("/approval1")
	public ResponseEntity<ResponseDTO> updateApproval1(@RequestParam Long id,@RequestParam String approval,@RequestParam String createdby,@RequestParam String userType) {
		String methodName = "updateApproval1()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		CRPreAppVO crPreAppvo= new CRPreAppVO();
		try {
			crPreAppvo = crePreAppService.updateApprove1(id, approval, createdby,userType);
			responseObjectsMap.put("CRPreAppVO", crPreAppvo);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

}
