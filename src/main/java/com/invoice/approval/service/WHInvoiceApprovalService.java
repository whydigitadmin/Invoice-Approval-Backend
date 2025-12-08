package com.invoice.approval.service;

import java.util.List;
import java.util.Map;


import com.invoice.approval.entity.WHInvoiceHdrVO;

public interface WHInvoiceApprovalService {
List<Map<String,Object>>getPendingApprovalReport(String userType,String userName);
	
	List<Map<String,Object>>getApprovalReport(String userType,String userName);

	List<Map<String,Object>>getAdminPendingDetailsApprove1slab(String userName);
	
	List<Map<String,Object>>getUserBranch(String userName);
	
	WHInvoiceHdrVO updateApprove1(Long id,String approval,String createdby,String userType);

}




