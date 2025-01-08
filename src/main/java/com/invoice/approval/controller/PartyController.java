package com.invoice.approval.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.PartyDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.repo.PartyMasterRepo;
import com.invoice.approval.service.PartyService;


@CrossOrigin
@RestController
@RequestMapping("/api/party")
public class PartyController extends BaseController {
	public static final Logger LOGGER = LoggerFactory.getLogger(PartyController.class);

	@Autowired
	PartyService partyService;
	
	@Autowired
	PartyMasterRepo PartyMasterRepo; 
	

	@PutMapping("/updateCreateParty")
	public ResponseEntity<ResponseDTO> updateCreateParty(@RequestBody PartyDTO partyDTO) {
		String methodName = "updateCreateParty()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;

		try {
			Map<String, Object> partyHdrVO = partyService.updateCreateParty(partyDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, partyHdrVO.get("message"));
			responseObjectsMap.put("partyHdrVO", partyHdrVO.get("partyHdrVO")); // Corrected key
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
