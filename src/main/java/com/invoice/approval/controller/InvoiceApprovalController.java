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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.GstInvoiceHdrVO;
import com.invoice.approval.service.InvoiceApprovalService;

@RestController
@RequestMapping("/api/InvoiceApproval")
public class InvoiceApprovalController extends BaseController {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(InvoiceApprovalController.class);
	
	@Autowired
	InvoiceApprovalService invoiceApprovalService;
	
	
	
	@GetMapping("/getPendingDetails")
	public ResponseEntity<ResponseDTO> getPendingDetails(@RequestParam String userType,String branchCode) {
		String methodName = "getPendingDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> pendingApprovalDetails = new ArrayList<Map<String, Object>>();
		try {
			pendingApprovalDetails = invoiceApprovalService.getPendingApprovalReport(userType,branchCode);

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
	
	
	
	@GetMapping("/getInvDetailsApprove1")
	public ResponseEntity<ResponseDTO> getInvDetailsApprove1(@RequestParam String userType,String branchCode) {
		String methodName = "getInvDetailsApprove1()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> approvedApprovalDetails1 = new ArrayList<Map<String, Object>>();
		try {
			approvedApprovalDetails1 = invoiceApprovalService.getApprovalReport(userType,branchCode);

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
	
	
	
	@GetMapping("/getInvDetailsApprove2")
	public ResponseEntity<ResponseDTO> getInvDetailsApprove2(@RequestParam String userType,String branchCode) {
		String methodName = "getInvDetailsApprove2()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> approvedApprovalDetails2 = new ArrayList<Map<String, Object>>();
		try {
			approvedApprovalDetails2 = invoiceApprovalService.getApprovalReport(userType,branchCode);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Approved Approval2 Details  found Successfullly");
			responseObjectsMap.put("approvedApprovalDetails2", approvedApprovalDetails2);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Approved Approval2 Details information receive failed",
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
		GstInvoiceHdrVO gstInvoiceHdrVO= new GstInvoiceHdrVO();
		try {
			gstInvoiceHdrVO = invoiceApprovalService.updateApprove1(id, approval, createdby,userType);
			responseObjectsMap.put("gstInvoiceHdrVO", gstInvoiceHdrVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@PutMapping("/approval3")
	public ResponseEntity<ResponseDTO> updateApproval3(@RequestParam Long id,@RequestParam String approval,@RequestParam String createdby) {
		String methodName = "updateApproval3()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		GstInvoiceHdrVO gstInvoiceHdrVO= new GstInvoiceHdrVO();
		try {
			gstInvoiceHdrVO = invoiceApprovalService.updateApprove3(id, approval, createdby);
			responseObjectsMap.put("gstInvoiceHdrVO", gstInvoiceHdrVO);
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
