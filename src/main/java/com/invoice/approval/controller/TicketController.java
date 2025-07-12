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
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.CRPreAppDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.dto.TicketDTO;
import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.entity.TicketVO;
import com.invoice.approval.repo.TicketRepo;
import com.invoice.approval.service.TicketService;

@CrossOrigin
@RestController
@RequestMapping("/api/Ticket")
public class TicketController extends BaseController {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PartyController.class);
	
	@Autowired
	TicketService ticketService;
	
	@Autowired
	TicketRepo ticketRepo;
	
	@PutMapping("/updateTicket")
	public ResponseEntity<ResponseDTO> updateTicket(@RequestBody TicketDTO ticketDTO) {
		String methodName = "updateTicket()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;

		try {
			Map<String, Object> ticketVO = ticketService.updateCreateTicket(ticketDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, ticketVO.get("message"));
			responseObjectsMap.put("ticketVO", ticketVO.get("ticketVO")); // Corrected key
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@PutMapping("/approval1")
	public ResponseEntity<ResponseDTO> updateApproval1(@RequestParam Long id,@RequestParam String approval,@RequestParam String createdby,@RequestParam String status,String solvedon) {
		String methodName = "updateApproval1()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		TicketVO ticketVO= new TicketVO();
		try {
			ticketVO = ticketService.updateApprove1(id, approval, createdby,status,solvedon);
			responseObjectsMap.put("ticketVO", ticketVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@PutMapping("/updateNote")
	public ResponseEntity<ResponseDTO> updateNote(@RequestParam Long id,@RequestParam String createdby) {
		String methodName = "updateNote()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		TicketVO ticketVO= new TicketVO();
		try {
			ticketVO = ticketService.updateNote(id, createdby);
			responseObjectsMap.put("ticketVO", ticketVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	@GetMapping("/getUserActiveStatus")
	public ResponseEntity<ResponseDTO> getUserActiveStatus(@RequestParam String userName) {
		String methodName = "getUserActiveStatus()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> getUserActiveStatus = new ArrayList<Map<String, Object>>();
		try {
			getUserActiveStatus = ticketService.getUserActiveStatus(userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "User Details  found Successfullly");
			responseObjectsMap.put("getUserActiveStatus", getUserActiveStatus);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "User Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/getTicketReport")
	public ResponseEntity<ResponseDTO> getTicketReport(@RequestParam String userName) {
		String methodName = "getPendingDetails()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> pendingApprovalDetails = new ArrayList<Map<String, Object>>();
		try {
			pendingApprovalDetails = ticketService.getTicketReport(userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Ticket Details  found Successfullly");
			responseObjectsMap.put("pendingApprovalDetails", pendingApprovalDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Ticket Details information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	@GetMapping("/getAdminNote")
	public ResponseEntity<ResponseDTO> getAdminNote(@RequestParam String userName) {
		String methodName = "getAdminNote()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> pendingApprovalDetails = new ArrayList<Map<String, Object>>();
		try {
			pendingApprovalDetails = ticketService.getAdminNote(userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Admin Notificaiton Details  found Successfullly");
			responseObjectsMap.put("pendingApprovalDetails", pendingApprovalDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Admin Notificaiton information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	
	
	@GetMapping("/getUserNote")
	public ResponseEntity<ResponseDTO> getUserNote(@RequestParam String userName) {
		String methodName = "getUserNote()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> pendingApprovalDetails = new ArrayList<Map<String, Object>>();
		try {
			pendingApprovalDetails = ticketService.getUserNote(userName);

		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isEmpty(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "User Notificaiton Details  found Successfullly");
			responseObjectsMap.put("pendingApprovalDetails", pendingApprovalDetails);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "User Notificaiton information receive failed",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@PutMapping("/uploadfile")
	public ResponseEntity<ResponseDTO> uploadfile(@RequestParam("files") List<MultipartFile> file,@RequestParam Long id) {
		String methodName = "uploadfile()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			ticketService.saveUploadFiles(file, id);
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	
	@GetMapping("/findByTicketById")
    public ResponseEntity<ResponseDTO> findByTicketById(@RequestParam(required = true) Long id) {
        String methodName = "findByTicketById()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        String errorMsg = null;
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO = null;
        TicketVO ticketVO = new TicketVO();
        try {
        	ticketVO = ticketService.getfindByTicketId(id);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
        }
        if (StringUtils.isBlank(errorMsg)) {
            responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Ticket information Retrieved successfully");
            responseObjectsMap.put("ticketVO", ticketVO);
            responseDTO = createServiceResponse(responseObjectsMap);
        } else {
            responseDTO = createServiceResponseError(responseObjectsMap, "Ticket Details information Retrieval failed", errorMsg);
        }
        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
        return ResponseEntity.ok().body(responseDTO);
    }


}
