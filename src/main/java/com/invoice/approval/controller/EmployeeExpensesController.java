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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.EmployeeExpensesDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.EmployeeExpensesVO;
import com.invoice.approval.entity.GstInvoiceHdrVO;
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
	
	@GetMapping("/getExpenseById")
    public ResponseEntity<ResponseDTO> getExpenseById(@RequestParam(required = true) Long id) {
        String methodName = "getExpenseById()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        String errorMsg = null;
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO = null;
        EmployeeExpensesVO employeeExpensesVO = new EmployeeExpensesVO();
        try {
        	employeeExpensesVO = employeeExpenseService.getEmployeeExpenseVOById(id);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
        }
        if (StringUtils.isBlank(errorMsg)) {
            responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Employee Expenses Details information Retrieved successfully");
            responseObjectsMap.put("employeeExpensesVO", employeeExpensesVO);
            responseDTO = createServiceResponse(responseObjectsMap);
        } else {
            responseDTO = createServiceResponseError(responseObjectsMap, "Employee Expenses Details information Retrieval failed", errorMsg);
        }
        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
        return ResponseEntity.ok().body(responseDTO);
    }
	
	@GetMapping("/getAllExpense")
    public ResponseEntity<ResponseDTO> getAllExpense() {
        String methodName = "getExpenseById()";
        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
        String errorMsg = null;
        Map<String, Object> responseObjectsMap = new HashMap<>();
        ResponseDTO responseDTO = null;
        List<EmployeeExpensesVO> employeeExpensesVO = new ArrayList<>();
        try {
        	employeeExpensesVO = employeeExpenseService.getAllEmployeeExpenseVO();
        } catch (Exception e) {
            errorMsg = e.getMessage();
            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
        }
        if (StringUtils.isBlank(errorMsg)) {
            responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Employee Expenses Details information Retrieved successfully");
            responseObjectsMap.put("employeeExpensesVO", employeeExpensesVO);
            responseDTO = createServiceResponse(responseObjectsMap);
        } else {
            responseDTO = createServiceResponseError(responseObjectsMap, "Employee Expenses Details information Retrieval failed", errorMsg);
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
		EmployeeExpensesVO empexpVO= new EmployeeExpensesVO();
		try {
			empexpVO = employeeExpenseService.updateApprove1(id, approval, createdby,userType);
			responseObjectsMap.put("EmployeeExpensesVO", empexpVO);
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
