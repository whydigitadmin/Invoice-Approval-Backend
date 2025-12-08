package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import com.invoice.approval.entity.GstInvoiceHdrVO;
import com.invoice.approval.entity.TTInvoiceHdrVO;

public interface TTInvoiceApprovalService {
	
List<Map<String,Object>>getPendingApprovalReport(String userType,String userName);


	
	List<Map<String,Object>>getApprovalReport(String userType,String userName);
	
	List<Map<String,Object>>getUserBranch(String userName);
	
	TTInvoiceHdrVO updateApprove1(Long id,String approval,String createdby,String userType);

	List<Map<String, Object>> getAdminPendingDetailsApprove1slab1(String userType, String userName);
}
