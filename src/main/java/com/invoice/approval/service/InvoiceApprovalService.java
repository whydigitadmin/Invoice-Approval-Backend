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
	
	List<Map<String, Object>> getAllARParties();
	
	List<Map<String, Object>> getInvoices(String userName,String branchName);
	
	List<Map<String, Object>> getDayBookBranchWise(String branchName, String fromdate, String todate);
	
	List<Map<String, Object>> getAPAgeingInternal(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6);

	List<Map<String, Object>> getARAgeingInternal(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6);
	
	List<Map<String, Object>> getAROS(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6);
	
	List<Map<String, Object>> getAPOS(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6);
	
    List<Map<String, Object>> getPartyLedger(String branchName,String sbcode,String fromdate,String todate,String subledgerType,String WithDet);
	
	List<Map<String, Object>> getPartyLedgerPartyName(String pType);
	
}
