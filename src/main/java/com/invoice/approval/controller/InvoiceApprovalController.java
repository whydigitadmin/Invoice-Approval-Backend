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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.ExpenseUploadVO;
import com.invoice.approval.entity.GstInvoiceHdrVO;
import com.invoice.approval.entity.IRNQRVO;
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

	
	
	@GetMapping("/getApprove1Db")
	public ResponseEntity<ResponseDTO> getApprove1Db(@RequestParam String userName) {
		String methodName = "getApprove1Db()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> getApprove1Db = new ArrayList<Map<String, Object>>();
		try {
			getApprove1Db = invoiceApprovalService.getApprove1Db(userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Approve1 DB  found Successfullly");
			responseObjectsMap.put("getApprove1Db", getApprove1Db);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Approve1 DB Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getApprove1TblDb")
	public ResponseEntity<ResponseDTO> getApprove1TblDb(@RequestParam String userName) {
		String methodName = "getApprove1TblDb()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> getApprove1TblDb = new ArrayList<Map<String, Object>>();
		try {
			getApprove1TblDb = invoiceApprovalService.getApprove1TblDb(userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Approve1 TBL DB  found Successfullly");
			responseObjectsMap.put("getApprove1TblDb", getApprove1TblDb);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Approve1 TBL DB Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	@GetMapping("/getHaiProductSummary")
	public ResponseEntity<ResponseDTO> getHaiProductSummary(@RequestParam String product) {
		String methodName = "getHaiInvCustomerDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> gethaiProductSummary = new ArrayList<Map<String, Object>>();
		try {
			gethaiProductSummary = invoiceApprovalService.getHaiProductSummary(product);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "HAI Product Summary found Successfullly");
			responseObjectsMap.put("gethaiProductSummary", gethaiProductSummary);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "HAI Product Summary information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	
	
	@GetMapping("/getHaiCustomerYearProfit")
	public ResponseEntity<ResponseDTO> getHaiCustomerYearProfit(@RequestParam String pName,String pType) {
		String methodName = "getHaiCustomerYearProfit()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> gethaiCustomerYearProfit = new ArrayList<Map<String, Object>>();
		try {
			gethaiCustomerYearProfit = invoiceApprovalService.getHaiCustomerYearProfit(pName,pType);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "HAI Year Profit Details found Successfullly");
			responseObjectsMap.put("gethaiCustomerYearProfit", gethaiCustomerYearProfit);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "HAI Year Profit Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getHaiInvCustomerDetails")
	public ResponseEntity<ResponseDTO> getHaiInvCustomerDetails(@RequestParam String pName,String pType) {
		String methodName = "getHaiInvCustomerDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> gethaiInvCustomerDetails = new ArrayList<Map<String, Object>>();
		try {
			gethaiInvCustomerDetails = invoiceApprovalService.getHaiInvCustomerDetails(pName,pType);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "HAI Inv Customer Details found Successfullly");
			responseObjectsMap.put("gethaiInvCustomerDetails", gethaiInvCustomerDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "HAI Inv Customer Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getJobFullDetails")
	public ResponseEntity<ResponseDTO> getJobFullDetails(@RequestParam String jobNo) {
		String methodName = "getJobFullDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> getJobFullDetails = new ArrayList<Map<String, Object>>();
		try {
			getJobFullDetails = invoiceApprovalService.getJobFullDetails(jobNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Full Job Details found Successfullly");
			responseObjectsMap.put("getJobFullDetails", getJobFullDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Full Job Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	

	
	
	@GetMapping("/getHaiCustomerRankDetails")
	public ResponseEntity<ResponseDTO> getHaiCustomerRankDetails(@RequestParam String pName,@RequestParam String pType) {
		String methodName = "getHaiCustomerRankDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> gethaiCustomerRankDetails = new ArrayList<Map<String, Object>>();
		try {
			gethaiCustomerRankDetails = invoiceApprovalService.getHaiCustomerRankDetails(pName,pType);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "HAI Customer Rank Details found Successfullly");
			responseObjectsMap.put("gethaiCustomerRankDetails", gethaiCustomerRankDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "HAI Branch Customer Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getHaiBranchCustomerDetails")
	public ResponseEntity<ResponseDTO> getHaiBranchCustomerDetails(@RequestParam String pName,@RequestParam String pType) {
		String methodName = "getHaiBranchCustomerDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> gethaiBranchCustomerDetails = new ArrayList<Map<String, Object>>();
		try {
			gethaiBranchCustomerDetails = invoiceApprovalService.getHaiBranchCustomerDetails(pName,pType);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "HAI Branch Customer Details found Successfullly");
			responseObjectsMap.put("gethaiBranchCustomerDetails", gethaiBranchCustomerDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "HAI Branch Customer Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getHaiCustomerDetails")
	public ResponseEntity<ResponseDTO> getHaiCustomerDetails(@RequestParam String pName,@RequestParam String pType) {
		String methodName = "getHaiCustomerDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> gethaiCustomerDetails = new ArrayList<Map<String, Object>>();
		try {
			gethaiCustomerDetails = invoiceApprovalService.getHaiCustomerDetails(pName,pType);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "HAI Customer Details found Successfullly");
			responseObjectsMap.put("gethaiCustomerDetails", gethaiCustomerDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "HAI Customer Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}


	@GetMapping("/getApprove1ChartDb")
	public ResponseEntity<ResponseDTO> getApprove1ChartDb(@RequestParam String userName) {
		String methodName = "getApprove1CharDb()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> getApprove1ChartDb = new ArrayList<Map<String, Object>>();
		try {
			getApprove1ChartDb = invoiceApprovalService.getApprove1ChartDb(userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Approve1 Chart DB  found Successfullly");
			responseObjectsMap.put("getApprove1ChartDb", getApprove1ChartDb);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Approve1 Chart DB Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getGSTR1Parties")
	public ResponseEntity<ResponseDTO> getGSTR1Parties() {
		String methodName = "getGSTR1Parties()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> GSTR1PartiesDetails = new ArrayList<Map<String, Object>>();
		try {
			GSTR1PartiesDetails = invoiceApprovalService.getGSTR1Parties();

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "GSTR1 Party  found Successfullly");
			responseObjectsMap.put("GSTR1PartiesDetails", GSTR1PartiesDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "GSTR1 Party Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getGSTR1Filling")
	public ResponseEntity<ResponseDTO>getGSTR1Filling(@RequestParam String branchName,@RequestParam String sbcode,@RequestParam String fromdate,@RequestParam String todate) {
		String methodName = "getGSTR1Filling()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> GSTR1details= new ArrayList<Map<String, Object>>();
		try {
			GSTR1details = invoiceApprovalService.getGSTR1Filling(branchName,sbcode,fromdate,todate);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "GSTR1 Details  found Successfullly");
			responseObjectsMap.put("GSTR1details", GSTR1details);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "GSTR1 Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	@GetMapping("/getIRNJobContDetails")
	public ResponseEntity<ResponseDTO>getIRNJobContDetails(@RequestParam String docNo) {
		String methodName = "getIRNJobInfo()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> irnjobcontdtls= new ArrayList<Map<String, Object>>();
		try {
			irnjobcontdtls = invoiceApprovalService.getIRNJobContDetails(docNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "IRN Job Cont Details found Successfullly");
			responseObjectsMap.put("irnjobcontdtls", irnjobcontdtls);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "IRN Job Cont Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	@GetMapping("/getIRNJobInfo")
	public ResponseEntity<ResponseDTO>getIRNJobInfo(@RequestParam String jobNo) {
		String methodName = "getIRNJobInfo()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> irnjobinfodtls= new ArrayList<Map<String, Object>>();
		try {
			irnjobinfodtls = invoiceApprovalService.getIRNJobInfo(jobNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "IRN Job Info Details found Successfullly");
			responseObjectsMap.put("irnjobinfodtls", irnjobinfodtls);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "IRN Job Info Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getIRNJobDetails")
	public ResponseEntity<ResponseDTO>getIRNJobDetails(@RequestParam String docNo) {
		String methodName = "getIRNJobDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> irnjobdtls= new ArrayList<Map<String, Object>>();
		try {
			irnjobdtls = invoiceApprovalService.getIRNJobDetails(docNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "IRN Job Details found Successfullly");
			responseObjectsMap.put("irnjobdtls", irnjobdtls);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "IRN Job Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getIRNQRbyDocNo")
	public ResponseEntity<ResponseDTO>getIRNQRbyDocNo(@RequestParam String docNo) {
		String methodName = "getIRNQRbyDocNo()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		IRNQRVO irnVo = new IRNQRVO();
		try {
			irnVo = invoiceApprovalService.getIRNQRbyDocNo(docNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "IRN QR  found Successfullly");
			responseObjectsMap.put("irnVo", irnVo);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "IRN QR Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	
	@GetMapping("/getIRNGridDetails")
	public ResponseEntity<ResponseDTO>getIRNGridDetails(@RequestParam String docNo) {
		String methodName = "getIRNGridDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> irngridlistdetails= new ArrayList<Map<String, Object>>();
		try {
			irngridlistdetails = invoiceApprovalService.getIRNGridDetails(docNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "IRN Grid  found Successfullly");
			responseObjectsMap.put("irngridlistdetails", irngridlistdetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "IRN Grid Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getIRNDetailsList")
	public ResponseEntity<ResponseDTO>getIRNDetailsList(@RequestParam String branchCode) {
		String methodName = "getIRNDetailsList()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> irnlistdetails= new ArrayList<Map<String, Object>>();
		try {
			irnlistdetails = invoiceApprovalService.getIRNDetailsList(branchCode);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "IRN Details  found Successfullly");
			responseObjectsMap.put("irnlistdetails", irnlistdetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "IRN Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getIRNDetails")
	public ResponseEntity<ResponseDTO>getIRNDetails(@RequestParam String docNo) {
		String methodName = "getIRNDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> irndetails= new ArrayList<Map<String, Object>>();
		try {
			irndetails = invoiceApprovalService.getIRNDetails(docNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "GSTR1 Details  found Successfullly");
			responseObjectsMap.put("irndetails", irndetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "GSTR1 Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getProfitAndLoss")
	public ResponseEntity<ResponseDTO>getProfitAndLoss(@RequestParam String branchName,@RequestParam String fromdate,@RequestParam String todate) {
		String methodName = "getProfitAndLoss()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> pldetails= new ArrayList<Map<String, Object>>();
		try {
			pldetails = invoiceApprovalService.getProfitAndLoss(branchName,fromdate,todate);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "PL Details  found Successfullly");
			responseObjectsMap.put("pldetails", pldetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Pl Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}


	
	@GetMapping("/getAllOpenJobs")
	public ResponseEntity<ResponseDTO>getAllOpenJobs(@RequestParam String branchName) {
		String methodName = "getAllOpenJobs()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> jobdetails= new ArrayList<Map<String, Object>>();
		try {
			jobdetails = invoiceApprovalService.getAllOpenJobs(branchName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Job Details  found Successfullly");
			responseObjectsMap.put("jobdetails", jobdetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Job Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}


	@GetMapping("/getJobCostDetails")
	public ResponseEntity<ResponseDTO>getJobCostDetails(@RequestParam String branchName,String jobNo) {
		String methodName = "getJobCostDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> jobdetails= new ArrayList<Map<String, Object>>();
		try {
			jobdetails = invoiceApprovalService.getJobCostDetails(branchName,jobNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Job Details  found Successfullly");
			responseObjectsMap.put("jobdetails", jobdetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Job Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}


	@GetMapping("/getJobCostSummary")
	public ResponseEntity<ResponseDTO>getJobCostSummary(@RequestParam String branchName,String jobNo) {
		String methodName = "getJobCostSummary()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> jobsumamarydetails= new ArrayList<Map<String, Object>>();
		try {
			jobsumamarydetails = invoiceApprovalService.getJobCostSummary(branchName,jobNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Job Summary Details  found Successfullly");
			responseObjectsMap.put("jobsumamarydetails", jobsumamarydetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Job Summary Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}


	
	@GetMapping("/getJobUnApproveDetails")
	public ResponseEntity<ResponseDTO>getJobUnApproveDetails(@RequestParam String jobNo) {
		String methodName = "getJobUnApproveDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> jobdetails= new ArrayList<Map<String, Object>>();
		try {
			jobdetails = invoiceApprovalService.getJobUnApproveDetails(jobNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Un Approve Job Details  found Successfullly");
			responseObjectsMap.put("jobdetails", jobdetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Un Approve Job Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	@GetMapping("/getJobCloseddt")
	public ResponseEntity<ResponseDTO>getJobCloseddt(@RequestParam String jobNo,String closed) {
		String methodName = "getJobCloseddt()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> jobcloseddtdetails= new ArrayList<Map<String, Object>>();
		try {
			jobcloseddtdetails = invoiceApprovalService.getJobCloseddt(jobNo,closed);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Job Closed Date Details  found Successfullly");
			responseObjectsMap.put("jobcloseddtdetails", jobcloseddtdetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Job Closed Date Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getJobIncome")
	public ResponseEntity<ResponseDTO>getJobIncome(@RequestParam String jobNo) {
		String methodName = "getJobIncome()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> jobincomedetails= new ArrayList<Map<String, Object>>();
		try {
			jobincomedetails = invoiceApprovalService.getJobIncome(jobNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Job Income Details  found Successfullly");
			responseObjectsMap.put("jobincomedetails", jobincomedetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Job Income Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}


	
	@GetMapping("/getJobExpense")
	public ResponseEntity<ResponseDTO>getJobExpense(@RequestParam String jobNo) {
		String methodName = "getJobExpense()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> jobexpensedetails= new ArrayList<Map<String, Object>>();
		try {
			jobexpensedetails = invoiceApprovalService.getJobExpense(jobNo);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Job Expense Details  found Successfullly");
			responseObjectsMap.put("jobexpensedetails", jobexpensedetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Job Expense Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getTrailBalance")
	public ResponseEntity<ResponseDTO>getTrailBalance(@RequestParam String branchName,@RequestParam String finyr,@RequestParam String fromdate,@RequestParam String todate,@RequestParam String WithDet) {
		String methodName = "getTrailBalance()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> tbdetails= new ArrayList<Map<String, Object>>();
		try {
			tbdetails = invoiceApprovalService.getTrailBalance(branchName,finyr,fromdate,todate,WithDet);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "TB Details  found Successfullly");
			responseObjectsMap.put("tbdetails", tbdetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "TB Details Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getLedgerReport")
	public ResponseEntity<ResponseDTO>getLedgerReport(@RequestParam String branchName,@RequestParam String accountName,@RequestParam String fromdate,@RequestParam String todate,@RequestParam String WithDet) {
		String methodName = "getLedgerReport()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> lrdetails= new ArrayList<Map<String, Object>>();
		try {
			lrdetails = invoiceApprovalService.getLedgerReport(branchName,accountName,fromdate,todate,WithDet);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Ledger Report  found Successfullly");
			responseObjectsMap.put("lrdetails", lrdetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Ledger Report Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getLedgerAccountName")
	public ResponseEntity<ResponseDTO> getLedgerAccountName() {
		String methodName = "getLedgerAccountName()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> accDetail = new ArrayList<Map<String, Object>>();
		try {
			accDetail = invoiceApprovalService.getLedgerAccountName();

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "ACcount Name  found Successfullly");
			responseObjectsMap.put("accDetail", accDetail);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Account Name information receive failed",
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
	
	
	
	@PostMapping("/excelUploadForExpense")
	public ResponseEntity<ResponseDTO> excelUploadForCCoa(@RequestParam MultipartFile files,
			@RequestParam(required = false) String createdBy) {

		int totalRows = 0;
		int successfulUploads = 0;
		String methodName = "excelUploadForCCoa()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);

		ResponseDTO responseDTO;
		Map<String, Object> responseObjectsMap = new HashMap<>();

		List<ExpenseUploadVO> uploadResult = new ArrayList<>();
		try {
			// Call the service method and get the result
			 uploadResult = invoiceApprovalService.ExcelUploadforExpense(files, createdBy);

			totalRows = invoiceApprovalService.getTotalRows(); // Get total rows processed
			successfulUploads = invoiceApprovalService.getSuccessfulUploads();
			responseObjectsMap.put("statusFlag", "Ok");
			responseObjectsMap.put("status", true);
			responseObjectsMap.put("totalRows", totalRows);
			responseObjectsMap.put("successfulUploads", successfulUploads);
			responseObjectsMap.put("message", "Excel Upload For CCoa successful"); 
//			// Populate success response
//			responseObjectsMap.put("statusFlag", "Ok");
//			responseObjectsMap.put("status", true);
//			responseObjectsMap.put("uploadResult", uploadResult);
			responseDTO = createServiceResponse(responseObjectsMap);

		} catch (Exception e) {
			String errorMsg = e.getMessage();
			LOGGER.error(CommonConstant.EXCEPTION, methodName, e);
			responseObjectsMap.put("statusFlag", "Error");
			responseObjectsMap.put("status", false);
			responseObjectsMap.put("errorMessage", errorMsg);

			responseDTO = createServiceResponseError(responseObjectsMap, "Excel Upload For CCoa Failed", errorMsg);
		}

		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	

}
