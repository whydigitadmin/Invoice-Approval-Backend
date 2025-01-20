package com.invoice.approval.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.EmployeeExpensesDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.service.EmployeeExpenseService;

@RestController
@RequestMapping("/api/expense")
public class EmployeeExpensesController extends BaseController {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeExpensesController.class);

	
	@Autowired
	EmployeeExpenseService employeeExpenseService;
	
	
	@PutMapping("/createEmpExpense")
	public ResponseEntity<ResponseDTO> createEmpExpense(@RequestBody EmployeeExpensesDTO employeeExpensesDTO) {
		String methodName = "createEmpExpense()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> expenseVO = employeeExpenseService.createUpdateEmployeeExpenses(employeeExpensesDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, expenseVO.get("message"));
			responseObjectsMap.put("expenseVO", expenseVO.get("employeeExpensesVO"));
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	@PutMapping("/uploadimage")
	public ResponseEntity<ResponseDTO> uploadimage(@RequestParam("files") List<MultipartFile> file,@RequestParam Long expenseId) {
		String methodName = "uploadimage()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			 employeeExpenseService.saveExpenseImages(file, expenseId);
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
