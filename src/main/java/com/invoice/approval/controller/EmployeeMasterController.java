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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.common.CommonConstant;
import com.invoice.approval.common.UserConstants;
import com.invoice.approval.dto.EmployeeMasterDTO;
import com.invoice.approval.dto.ResponseDTO;
import com.invoice.approval.entity.EmployeeMasterVO;
import com.invoice.approval.repo.EmployeeMasterRepo;
import com.invoice.approval.service.EmployeeMasterService;
@RestController
@RequestMapping("/api/employeemaster")
public class EmployeeMasterController extends BaseController {

	

	public static final Logger LOGGER = LoggerFactory.getLogger(PartyController.class);

	@Autowired
	EmployeeMasterService empMasService;
	
	@Autowired
	EmployeeMasterRepo empMasRepo; 
	

	@PutMapping("/updateEmpMaster")
	public ResponseEntity<ResponseDTO> updateEmpMaster(@RequestBody EmployeeMasterDTO empMasterDTO) {
		String methodName = "updateEmpMaster()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;

		try {
			Map<String, Object> empMasVO = empMasService.updateCreateEmpMaster(empMasterDTO);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, empMasVO.get("message"));
			responseObjectsMap.put("empMasVO", empMasVO.get("empMasVO")); // Corrected key
			responseDTO = createServiceResponse(responseObjectsMap);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
			responseDTO = createServiceResponseError(responseObjectsMap, errorMsg, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
	


	@PostMapping("/excelUploadForEmployeeMaster")
		public ResponseEntity<ResponseDTO> excelUploadForEmployeeMaster(@RequestParam MultipartFile[] files,@RequestParam(required = false) String createdBy) {
			String methodName = "excelUploadForEmployeeMaster()";
			int totalRows = 0;
			Map<String, Object> responseObjectsMap = new HashMap<>();
			int successfulUploads = 0;
			ResponseDTO responseDTO = null;
			try {
				// Call service method to process Excel upload
				empMasService.excelUploadForEmployeeMaster(files,createdBy);

				// Retrieve the counts after processing
				totalRows = empMasService.getTotalRows(); // Get total rows processed
				successfulUploads = empMasService.getSuccessfulUploads(); // Get successful uploads count
				responseObjectsMap.put("statusFlag", "Ok");
		        responseObjectsMap.put("status", true);
		        responseObjectsMap.put("totalRows", totalRows);
		        responseObjectsMap.put("successfulUploads", successfulUploads);
		        responseObjectsMap.put("message", "Excel Upload For Employee Master successful"); // Directly include the message here
		        responseDTO = createServiceResponse(responseObjectsMap);

		    } catch (Exception e) {
		        String errorMsg = e.getMessage();
		        LOGGER.error(CommonConstant.EXCEPTION, methodName, e);
		        responseObjectsMap.put("statusFlag", "Error");
		        responseObjectsMap.put("status", false);
		        responseObjectsMap.put("errorMessage", errorMsg);

		        responseDTO = createServiceResponseError(responseObjectsMap, "Excel Upload For Employee Master Failed", errorMsg);
		    }
		    LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		    return ResponseEntity.ok().body(responseDTO);
		}
//		
		

	
	@PutMapping("/uploadEmpImage")
	 public ResponseEntity<ResponseDTO> uploadEmpImage(@RequestParam("file") MultipartFile file,@RequestParam Long EmployeeMasterid) {
	  String methodName = "uploadEmpImage()";
	  LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	  String errorMsg = null;
	  Map<String, Object> responseObjectsMap = new HashMap<>();
	  ResponseDTO responseDTO = null;
	  EmployeeMasterVO empMasVO = null;
	  try {
		  empMasVO = empMasService.uploadImageInBloob(file, EmployeeMasterid);
	  } catch (Exception e) {
	   errorMsg = e.getMessage();
	   LOGGER.error("Unable To Upload partdrawing", methodName, errorMsg);
	  }
	  if (StringUtils.isBlank(errorMsg)) {
	   responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Employee Image Successfully Upload");
	   responseObjectsMap.put("empMasVO", empMasVO);
	   responseDTO = createServiceResponse(responseObjectsMap);
	  } else {
	   responseDTO = createServiceResponseError(responseObjectsMap, "Employee Image Upload Failed", errorMsg);
	  }
	  LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	  return ResponseEntity.ok().body(responseDTO);
	 }

	
	
//	@GetMapping("/getAllEmployees")
//	public ResponseEntity<ResponseDTO> getPendingDetails() {
//		String methodName = "getAllEmployees()";
//		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//		String errorMsg = null;
//		Map<String, Object> responseObjectsMap = new HashMap<>();
//		ResponseDTO responseDTO = null;
//		List<Map<String, Object>> allEmp = new ArrayList<Map<String, Object>>();
//		try {
//			allEmp = empMasService.getAllEmployees();
//
//		} catch (Exception e) {
//			errorMsg = e.getMessage();
//			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
//		}
//		if (StringUtils.isEmpty(errorMsg)) {
//			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "All Employees  found Successfullly");
//			responseObjectsMap.put("allEmp", allEmp);
//			responseDTO = createServiceResponse(responseObjectsMap);
//		} else {
//			responseDTO = createServiceResponseError(responseObjectsMap, "All Employees information receive failed",
//					errorMsg);
//		}
//		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//		return ResponseEntity.ok().body(responseDTO);
//	}

	
	
//	@GetMapping("/getfindByEmployeeId")
//    public ResponseEntity<ResponseDTO> getfindByEmployeeId(@RequestParam(required = true) Long id) {
//        String methodName = "getfindByEmployeeId()";
//        LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//        String errorMsg = null;
//        Map<String, Object> responseObjectsMap = new HashMap<>();
//        ResponseDTO responseDTO = null;
//        EmployeeMasterVO empMasVO = new EmployeeMasterVO();
//        try {
//        	empMasVO = empMasService.getfindByEmployeeId(id);
//        } catch (Exception e) {
//            errorMsg = e.getMessage();
//            LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
//        }
//        if (StringUtils.isBlank(errorMsg)) {
//            responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Employee information Retrieved successfully");
//            responseObjectsMap.put("empMasVO", empMasVO);
//            responseDTO = createServiceResponse(responseObjectsMap);
//        } else {
//            responseDTO = createServiceResponseError(responseObjectsMap, "Employee Details information Retrieval failed", errorMsg);
//        }
//        LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//        return ResponseEntity.ok().body(responseDTO);
  //  }
	
	
	 @GetMapping("/getAllEmployees")
	 public ResponseEntity<ResponseDTO> getAllEmployees() {
	  String methodName = "getAllEmployees()";
	  LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
	  String errorMsg = null;
	  Map<String, Object> responseObjectsMap = new HashMap<>();
	  ResponseDTO responseDTO = null;
	  List<EmployeeMasterVO> empMasVO = new ArrayList<>();
	  try {
		  empMasVO = empMasService.getAllEmployees();
	  } catch (Exception e) {
	   errorMsg = e.getMessage();
	   LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
	  }
	  if (StringUtils.isBlank(errorMsg)) {
	   responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Employee information get successfully");
	   responseObjectsMap.put("empMasVO", empMasVO);
	   responseDTO = createServiceResponse(responseObjectsMap);
	  } else {
	   responseDTO = createServiceResponseError(responseObjectsMap, "Additional Goals information receive failed", errorMsg);
	  }
	  LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	  return ResponseEntity.ok().body(responseDTO);
	 }
}
