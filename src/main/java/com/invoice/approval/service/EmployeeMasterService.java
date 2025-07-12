package com.invoice.approval.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.EmployeeMasterDTO;
import com.invoice.approval.entity.EmployeeMasterVO;
import com.invoice.approval.exception.ApplicationException;

public interface EmployeeMasterService {
	
	public Map<String, Object> updateCreateEmpMaster(EmployeeMasterDTO empMasterDTO) throws ApplicationException;
	
//	void saveUploadFiles(MultipartFile files, Long id) throws ApplicationException, IOException;
	
	//List<EmployeeMasterVO> getfindByEmployeeId(Long id);

	EmployeeMasterVO uploadImageInBloob(MultipartFile file, Long EmployeeMasterId) throws IOException;

	List<EmployeeMasterVO> getAllEmployees();

	public void excelUploadForEmployeeMaster(MultipartFile[] files, String createdBy) throws ApplicationException;

	public int getTotalRows();
	
	public int getSuccessfulUploads();
	
	

}
