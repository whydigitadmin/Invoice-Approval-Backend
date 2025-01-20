package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.invoice.approval.dto.CRPreAppDTO;
import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface CRPreAppService {
	
	

	public Map<String, Object> updateCreateCRPreApp(CRPreAppDTO crPreAppDTO) throws ApplicationException;

	CRPreAppVO updateApprove1(Long id,String approval,String createdby,String userType);
	
List<Map<String,Object>>getPendingApprovalReport(String userType,String userName);
	
	List<Map<String,Object>>getApprovalReport(String userType,String userName);

}
