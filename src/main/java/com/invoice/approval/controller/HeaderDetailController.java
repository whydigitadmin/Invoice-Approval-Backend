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
import com.invoice.approval.dto.HeaderDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.HeaderVO;
import com.invoice.approval.service.HeaderDetailService;



@CrossOrigin
@RestController
@RequestMapping("/api/HeaderDetail")
public class HeaderDetailController extends BaseController{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(HeaderDetailController.class);
	
	@Autowired
	HeaderDetailService headerDetailService;
	
	@PutMapping("/createHeaderDetail")
	public ResponseEntity<ResponseDTO> createHeaderDetail(@RequestBody HeaderDTO headerDto ) {
		String methodName = "createHeaderDetail()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> headerVO = headerDetailService.createHeaderDetail(headerDto);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, headerVO.get("message"));
			responseObjectsMap.put("headerVO", headerVO.get("headerVO"));
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
		List<HeaderVO> headerVO = new ArrayList<>();
		try {
			 headerVO = headerDetailService.headerVO();
			responseObjectsMap.put("headerVO", headerVO);
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
		HeaderVO headerVO = new HeaderVO();
		try {
			 headerVO = headerDetailService.headerDetailsVO(id);
			responseObjectsMap.put("headerVO", headerVO);
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
