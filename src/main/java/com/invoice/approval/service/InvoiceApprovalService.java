package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.invoice.approval.entity.GstInvoiceHdrVO;

@Service
public interface InvoiceApprovalService {
	
	List<Map<String,Object>>getPendingApprovalReport(String userType);
	
	GstInvoiceHdrVO updateApprove1(Long id,String approval,String createdby,String userType);

}
