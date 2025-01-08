package com.invoice.approval.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.invoice.approval.entity.GstInvoiceHdrVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface InvoiceApprovalService {
	
	List<Map<String,Object>>getPendingApprovalReport(String userType,String userName);
	
	List<Map<String,Object>>getApprovalReport(String userType,String userName);
	
	List<Map<String,Object>>getUserBranch(String userName);
	
	List<Map<String,Object>>getAllCreditParties();
		
	GstInvoiceHdrVO updateApprove1(Long id,String approval,String createdby,String userType);

	GstInvoiceHdrVO updateApprove3(Long id, String approval, String createdby) throws ApplicationException;

	List<Map<String, Object>> getMIS(String branchName, String status, String fromdate, String todate);
	
	List<Map<String, Object>> getAllAPParties();
	
	List<Map<String, Object>> getAPAgeingInternal(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6);

}
