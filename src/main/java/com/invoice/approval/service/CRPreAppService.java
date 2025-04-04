package com.invoice.approval.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.CRPreAppDTO;
import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.entity.EmployeeExpensesVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface CRPreAppService {

	public Map<String, Object> updateCreateCRPreApp(CRPreAppDTO crPreAppDTO) throws ApplicationException;

	CRPreAppVO updateApprove1(Long id, String approval, String createdby, String userType);

	CRPreAppVO updateApprove2(Long id, String approval, String createdby, String userType);

	List<Map<String, Object>> getPendingApprovalReport(String userType, String userName);

	List<Map<String, Object>> getPendingApprovalReport2(String userType, String userName);

	List<Map<String, Object>> getApprovalReport(String userType, String userName);
	
	List<Map<String, Object>> getApprovalReport2(String userType, String userName);

//	void saveUploadFiles(MultipartFile files, Long id) throws ApplicationException, IOException;
	
	CRPreAppVO getfindByGSTPreCreditrId(Long id);

	List<Map<String, Object>> getCRReasons();

	void saveUploadFiles(List<MultipartFile> files, Long id) throws ApplicationException, IOException;

}
