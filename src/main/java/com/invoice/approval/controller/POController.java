
package com.invoice.approval.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.invoice.approval.dto.PODTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.dto.VendorDTO;
import com.invoice.approval.entity.POVO;
import com.invoice.approval.entity.VendorVO;
import com.invoice.approval.service.PODTLService;
import com.invoice.approval.service.VendorService;



@CrossOrigin
@RestController
@RequestMapping("/api/PO")
public class POController extends BaseController{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(POController.class);
	
	@Autowired
	PODTLService headerDetailService;
	
	@Autowired
	VendorService vendorService;
	
	@PutMapping("/createPO")
	public ResponseEntity<ResponseDTO> createPO(@RequestBody PODTO poDto ) {
		String methodName = "createPO()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> poVO = headerDetailService.createPO(poDto);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, poVO.get("message"));
			responseObjectsMap.put("poVO", poVO.get("poVO"));
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
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
		List<POVO> poVO = new ArrayList<>();
		try {
			poVO = headerDetailService.poVO();
			responseObjectsMap.put("poVO", poVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getAllDetails")
	public List<POVO> getAllDetails() {
		
			return headerDetailService.poVO();
			
	}
	
	
	
	@GetMapping("/getVendorAll")
	public ResponseEntity<ResponseDTO> getVendorAll( ) {
		String methodName = "getVendorAll()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<VendorVO> vendorVO = new ArrayList<>();
		try {
			vendorVO = vendorService.vendorVO();
			responseObjectsMap.put("vendorVO", vendorVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@GetMapping("/getById")
	public ResponseEntity<ResponseDTO> getById(@RequestParam Long id ) {
		String methodName = "getById(id)";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		POVO poVO = new POVO();
		try {
			poVO = headerDetailService.podtlsVO(id);
			responseObjectsMap.put("poVO", poVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	@PutMapping("/createvendor")
	public ResponseEntity<ResponseDTO> createvendor(@RequestBody VendorDTO vendorDTO) {
		String methodName = "createvendor()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> vendorVO = vendorService.createVO(vendorDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, vendorVO.get("message"));
			responseObjectsMap.put("vendorVO", vendorVO.get("preGoalsVO"));
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
