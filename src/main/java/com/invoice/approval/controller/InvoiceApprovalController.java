package com.invoice.approval.controller;

import java.util.ArrayList;
import java.util.Date;
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
	public ResponseEntity<ResponseDTO> getPendingDetails(@RequestParam String userType,@RequestParam String userName) {
		String methodName = "getPendingDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> pendingApprovalDetails = new ArrayList<Map<String, Object>>();
		try {
			pendingApprovalDetails = invoiceApprovalService.getPendingApprovalReport(userType,userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Pending PartyOS Details  found Successfullly");
			responseObjectsMap.put("pendingApprovalDetails", pendingApprovalDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Pending PartyOS Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getMIS")
	public ResponseEntity<ResponseDTO> getMIS(@RequestParam String branchName,String status,@RequestParam String fromDate,@RequestParam String toDate) {
		String methodName = "getMIS()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> misDetails = new ArrayList<Map<String, Object>>();
		try {
			misDetails = invoiceApprovalService.getMIS(branchName,status,fromDate,toDate);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "MIS  found Successfullly");
			responseObjectsMap.put("misDetails", misDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "MIS Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	
	@GetMapping("/getDayBookBranchWise")
	public ResponseEntity<ResponseDTO> getDayBookBranchWise(@RequestParam String branchName,@RequestParam String fromDate,@RequestParam String toDate) {
		String methodName = "getDayBookBranchWise()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> dayBookBranchWiseDetails = new ArrayList<Map<String, Object>>();
		try {
			dayBookBranchWiseDetails = invoiceApprovalService.getDayBookBranchWise(branchName,fromDate,toDate);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Day Book Data  found Successfullly");
			responseObjectsMap.put("dayBookBranchWiseDetails", dayBookBranchWiseDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Day Book Data Data receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getAPAgeingInternal")
	public ResponseEntity<ResponseDTO>getAPAgeingInternal (@RequestParam String sbcode,@RequestParam String div,@RequestParam String ptype,@RequestParam String pbranchname,@RequestParam String asondt,@RequestParam String slab1,@RequestParam String slab2,@RequestParam String slab3,@RequestParam String slab4,@RequestParam String slab5,@RequestParam String slab6) {
		String methodName = "getAPAgeingInternal()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> apAgeingDetails = new ArrayList<Map<String, Object>>();
		try {
			apAgeingDetails = invoiceApprovalService.getAPAgeingInternal(sbcode,div,ptype,pbranchname,asondt,slab1,slab2,slab3,slab4,slab5,slab6);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "AP Ageing  found Successfullly");
			responseObjectsMap.put("apAgeingDetails", apAgeingDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "AP Ageing Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getARAgeingInternal")
	public ResponseEntity<ResponseDTO>getARAgeingInternal (@RequestParam String sbcode,@RequestParam String div,@RequestParam String ptype,@RequestParam String pbranchname,@RequestParam String asondt,@RequestParam String slab1,@RequestParam String slab2,@RequestParam String slab3,@RequestParam String slab4,@RequestParam String slab5,@RequestParam String slab6) {
		String methodName = "getARAgeingInternal()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> arAgeingDetails = new ArrayList<Map<String, Object>>();
		try {
			arAgeingDetails = invoiceApprovalService.getARAgeingInternal(sbcode,div,ptype,pbranchname,asondt,slab1,slab2,slab3,slab4,slab5,slab6);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "AR Ageing  found Successfullly");
			responseObjectsMap.put("arAgeingDetails", arAgeingDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "AR Ageing Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}


	@GetMapping("/getAROS")
	public ResponseEntity<ResponseDTO>getAROS(@RequestParam String sbcode,@RequestParam String div,@RequestParam String ptype,@RequestParam String pbranchname,@RequestParam String asondt,@RequestParam String slab1,@RequestParam String slab2,@RequestParam String slab3,@RequestParam String slab4,@RequestParam String slab5,@RequestParam String slab6) {
		String methodName = "getAROS()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> arOSDetails= new ArrayList<Map<String, Object>>();
		try {
			arOSDetails = invoiceApprovalService.getAROS(sbcode,div,ptype,pbranchname,asondt,slab1,slab2,slab3,slab4,slab5,slab6);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "AR OS  found Successfullly");
			responseObjectsMap.put("arOSDetails", arOSDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "AR OS Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getAPOS")
	public ResponseEntity<ResponseDTO>getAPOS(@RequestParam String sbcode,@RequestParam String div,@RequestParam String ptype,@RequestParam String pbranchname,@RequestParam String asondt,@RequestParam String slab1,@RequestParam String slab2,@RequestParam String slab3,@RequestParam String slab4,@RequestParam String slab5,@RequestParam String slab6) {
		String methodName = "getAPOS()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> apOSDetails= new ArrayList<Map<String, Object>>();
		try {
			apOSDetails = invoiceApprovalService.getAPOS(sbcode,div,ptype,pbranchname,asondt,slab1,slab2,slab3,slab4,slab5,slab6);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "AP OS  found Successfullly");
			responseObjectsMap.put("apOSDetails", apOSDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "AP OS Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	

	
	@GetMapping("/getPartyLedger")
	public ResponseEntity<ResponseDTO>getPartyLedger(@RequestParam String branchName,@RequestParam String sbcode,@RequestParam String fromdate,@RequestParam String todate,@RequestParam String subledgerType,@RequestParam String WithDet) {
		String methodName = "getPartyLedger()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> pldetails= new ArrayList<Map<String, Object>>();
		try {
			pldetails = invoiceApprovalService.getPartyLedger(branchName,sbcode,fromdate,todate,subledgerType,WithDet);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "PL  found Successfullly");
			responseObjectsMap.put("pldetails", pldetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "PL Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@GetMapping("/getPartyLedgerPartyName")
	public ResponseEntity<ResponseDTO>getPartyLedgerPartyName(@RequestParam String pType) {
		String methodName = "getPartyLedgerPartyName(pType)";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> plParties= new ArrayList<Map<String, Object>>();
		try {
			plParties = invoiceApprovalService.getPartyLedgerPartyName(pType);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "PL  found Successfullly");
			responseObjectsMap.put("plParties", plParties);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "AP OS Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getAllAPParties")
	public ResponseEntity<ResponseDTO> getAllAPParties() {
		String methodName = "getAllAPParties()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> partyDetails = new ArrayList<Map<String, Object>>();
		try {
			partyDetails = invoiceApprovalService.getAllAPParties();

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "AP Party  found Successfullly");
			responseObjectsMap.put("partyDetails", partyDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "AP Party Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	

	
	@GetMapping("/getAllARParties")
	public ResponseEntity<ResponseDTO> getAllARParties() {
		String methodName = "getAllARParties()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> partyDetails = new ArrayList<Map<String, Object>>();
		try {
			partyDetails = invoiceApprovalService.getAllARParties();

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "AR Party  found Successfullly");
			responseObjectsMap.put("partyDetails", partyDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "AR Party Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getAllCreditParties")
	public ResponseEntity<ResponseDTO> getAllCreditParties() {
		String methodName = "getAllCreditParties()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> partyDetails = new ArrayList<Map<String, Object>>();
		try {
			partyDetails = invoiceApprovalService.getAllCreditParties();

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Party  found Successfullly");
			responseObjectsMap.put("partyDetails", partyDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Party Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	
	@GetMapping("/getInvoices")
	public ResponseEntity<ResponseDTO> getInvoices(@RequestParam String userName,String branchName) {
		String methodName = "getInvoices()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> invDetails = new ArrayList<Map<String, Object>>();
		try {
			invDetails = invoiceApprovalService.getInvoices(userName,branchName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Invoice  found Successfullly");
			responseObjectsMap.put("invDetails", invDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Invoice Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getInvDetailsApprove1")
	public ResponseEntity<ResponseDTO> getInvDetailsApprove1(@RequestParam String userType,@RequestParam String userName) {
		String methodName = "getInvDetailsApprove1()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> approvedApprovalDetails1 = new ArrayList<Map<String, Object>>();
		try {
			approvedApprovalDetails1 = invoiceApprovalService.getApprovalReport(userType,userName);

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
	public ResponseEntity<ResponseDTO> getInvDetailsApprove2(@RequestParam String userType,@RequestParam String userName) {
		String methodName = "getInvDetailsApprove2()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> approvedApprovalDetails2 = new ArrayList<Map<String, Object>>();
		try {
			approvedApprovalDetails2 = invoiceApprovalService.getApprovalReport(userType,userName);

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
