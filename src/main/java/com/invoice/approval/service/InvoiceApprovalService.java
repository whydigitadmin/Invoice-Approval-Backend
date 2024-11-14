package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.invoice.approval.entity.GstInvoiceHdrVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface InvoiceApprovalService {
	
	List<Map<String,Object>>getPendingApprovalReport(String userType);
	
	List<Map<String,Object>>getApprovalReport(String userType);
	
	GstInvoiceHdrVO updateApprove1(Long id,String approval,String createdby,String userType);

	GstInvoiceHdrVO updateApprove3(Long id, String approval, String createdby) throws ApplicationException;

}
