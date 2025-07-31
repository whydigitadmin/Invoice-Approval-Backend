package com.invoice.approval.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.invoice.approval.dto.DocTypeDTO;
import com.invoice.approval.dto.DocTypeMappingDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.DocTypeMappingVO;
import com.invoice.approval.entity.DocTypeVO;
import com.invoice.approval.service.MasterService;

@RequestMapping("/api/master")
@RestController
public class MasterController extends BaseController {
	
	
	@Autowired
	MasterService masterService;
	
	
	@PutMapping("/createDocType")
	public ResponseEntity<ResponseDTO> createDocType(
			@RequestBody DocTypeDTO docTypeDTO) {
		String methodName = "createDocType()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		Map<String, Object> responseObjectsMap = new HashMap<String, Object>();
		String errorMsg = null;
		ResponseDTO responseDTO = null;
		try {
			DocTypeVO docTypeVO = masterService
					.createDocType(docTypeDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Document Type Created Successfully");
			responseObjectsMap.put("docTypeVO", docTypeVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getPendingMappingDetails")
	public ResponseEntity<ResponseDTO> getPendingMappingDetails(@RequestParam String branch,@RequestParam String branchCode,@RequestParam int finYear, @RequestParam int finYearId) {
		String methodName = "getPendingMappingDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String,Object>> pendingDocTypeMappingDetails=new ArrayList<>();
		try {
			pendingDocTypeMappingDetails = masterService.getPendingDocTypeMapping(branch, branchCode, finYear, finYearId);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE,
					"Calendar Notification information get successfully By OrgId");
			responseObjectsMap.put("pendingDocTypeMappingDetails", pendingDocTypeMappingDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					"Calendar Notification information receive failed By OrgId", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);

	}
	
	@PutMapping("/createDocTypeMapping")
	public ResponseEntity<ResponseDTO> createDocTypeMapping(
			@RequestBody DocTypeMappingDTO docTypeMappingDTO) {
		String methodName = "createDocTypeMapping()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		Map<String, Object> responseObjectsMap = new HashMap<String, Object>();
		String errorMsg = null;
		ResponseDTO responseDTO = null;
		try {
			DocTypeMappingVO docTypeMappingVO = masterService
					.createDocTypeMappingVO(docTypeMappingDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Document Type Mapping Created Successfully");
			responseObjectsMap.put("docTypeMappingVO", docTypeMappingVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getDocId")
	public ResponseEntity<ResponseDTO> getDocId(@RequestParam String branch,@RequestParam int finYear, @RequestParam String screenCode) {
		String methodName = "getPendingMappingDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		String docId=null;
		try {
			docId = masterService.getDocid(branch, finYear, screenCode);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE,
					"Docid Notification information get successfully By OrgId");
			responseObjectsMap.put("docId", docId);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap,
					"Docid Notification information receive failed By OrgId", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);

	}

}
