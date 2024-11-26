package com.invoice.approval.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.approval.entity.GstInvoiceHdrVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.GstInvoiceHdrRepo;

@Service
public class InvoiceApprovalServiceImpl implements InvoiceApprovalService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(InvoiceApprovalServiceImpl.class);
	
	@Autowired
	GstInvoiceHdrRepo gstInvoiceHdrRepo;
	
	
	@Override
	public List<Map<String, Object>> getPendingApprovalReport(String userType,String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userType.equals("approve1"))
		{
			details=gstInvoiceHdrRepo.getPendingDetailsApprove1(userName);
			
		}
		else
		{
			details=gstInvoiceHdrRepo.getPendingDetailsApprove2(userName);
			
		}
		
		return pendingDetails(details);
	}

	
	@Override
	public List<Map<String, Object>> getApprovalReport(String userType,String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userType.equals("approve1"))
		{
			
			details=gstInvoiceHdrRepo.getInvDetailsApprove1(userName);
		}
		else
		{
			
			details=gstInvoiceHdrRepo.getInvDetailsApprove2(userName);
		}
		
		return approveDetails(details);
	}
	
	private List<Map<String, Object>> pendingDetails(Set<Object[]> details) {
		List<Map<String,Object>>report=new ArrayList<>();
		for(Object[]det:details)
		{
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl= new HashMap<>();
			dtl.put("gstInvoiceHdrId",det[0]);
			dtl.put("branchCode", det[1] != null ? det[1].toString() : "");
			dtl.put("finYear", det[2] != null ? det[2].toString() : "");
			dtl.put("docId", det[3] != null ? det[3].toString() : "");
			dtl.put("docDate", det[4] != null ? dateFormat.format((Date) det[4]) : "");
			dtl.put("partyName", det[5] != null ? det[5].toString() : "");
			dtl.put("partyCode", det[6] != null ? det[6].toString() : "");
			dtl.put("outStanding", det[7] != null ? df.format(new BigDecimal(det[7].toString())) : "");
			dtl.put("totalInvAmtLc", det[8] != null ? df.format(new BigDecimal(det[8].toString())) : "");
			dtl.put("creditDays", det[9] != null ? Integer.parseInt(det[9].toString()) : 0);
			dtl.put("creditLimit", det[10] != null ? df.format(new BigDecimal(det[10].toString())) : "");
			
			report.add(dtl);
		}
		return report;
	}

	
	private List<Map<String, Object>> approveDetails(Set<Object[]> details) {
		List<Map<String,Object>>report=new ArrayList<>();
		for(Object[]det:details)
		{
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			
			Map<String, Object> dtl= new HashMap<>();
			dtl.put("gstInvoiceHdrId",det[0]);
			dtl.put("branchCode", det[1] != null ? det[1].toString() : "");
			dtl.put("finYear", det[2] != null ? det[2].toString() : "");
			dtl.put("docId", det[3] != null ? det[3].toString() : "");
			dtl.put("docDate", det[4] != null ? dateFormat.format((Date) det[4]) : "");
			dtl.put("partyName", det[5] != null ? det[5].toString() : "");
			dtl.put("partyCode", det[6] != null ? det[6].toString() : "");
			dtl.put("outStanding", det[7] != null ? df.format(new BigDecimal(det[7].toString())) : "");
			dtl.put("totalInvAmtLc", det[8] != null ? df.format(new BigDecimal(det[8].toString())) : "");
			dtl.put("creditDays", det[9] != null ? Integer.parseInt(det[9].toString()) : 0);
			dtl.put("creditLimit", det[10] != null ? df.format(new BigDecimal(det[10].toString())) : "");
			dtl.put("approve1on", det[15] != null ? det[15].toString() : "");
			dtl.put("approve2on", det[17] != null ? det[17].toString() : "");
			
			report.add(dtl);
		}
		return report;
	}
	
	
	@Override
	public GstInvoiceHdrVO updateApprove1(Long id, String approval, String createdby,String userType) {
		GstInvoiceHdrVO gstInvoiceHdrVO= gstInvoiceHdrRepo.findByGstInvoiceHdrId(id);
		if(userType.equals("approve1"))
		{
			if(approval.equals("1"))
			{
				gstInvoiceHdrVO.setApprove1("T");
				gstInvoiceHdrVO.setApprove1Name(createdby);
				gstInvoiceHdrVO.setApprove1On(LocalDateTime.now());
			}
			else {
				gstInvoiceHdrVO.setApprove1("F");
				gstInvoiceHdrVO.setApprove1Name(createdby);
				gstInvoiceHdrVO.setApprove1On(LocalDateTime.now());
			}
		}else
		{
			if(approval.equals("1"))
			{
				gstInvoiceHdrVO.setApprove2("T");
				gstInvoiceHdrVO.setApprove2Name(createdby);
				gstInvoiceHdrVO.setApprove2On(LocalDateTime.now());
			}
			else {
				gstInvoiceHdrVO.setApprove2("F");
				gstInvoiceHdrVO.setApprove2Name(createdby);
				gstInvoiceHdrVO.setApprove2On(LocalDateTime.now());
			}
			
		}
		
		return gstInvoiceHdrRepo.save(gstInvoiceHdrVO);
	}
	
	@Override
	public GstInvoiceHdrVO updateApprove3(Long id, String approval, String createdby) throws ApplicationException {
		GstInvoiceHdrVO gstInvoiceHdrVO= gstInvoiceHdrRepo.findByGstInvoiceHdrId(id);
		
		if (gstInvoiceHdrVO.getApprove3Name() == null) {
		    gstInvoiceHdrVO.setApprove3(approval.equals("1") ? "T" : "F");
		    gstInvoiceHdrVO.setApprove3Name(createdby);
		    gstInvoiceHdrVO.setApprove3On(LocalDateTime.now());
		} else {
		    switch (gstInvoiceHdrVO.getApprove3()) {
		        case "T":
		            throw new ApplicationException("This Invoice Already Approved");
		        case "F":
		            throw new ApplicationException("This Invoice Already Rejected");
		        default:
		            throw new ApplicationException("Invalid approval status");
		    }
		}
			return gstInvoiceHdrRepo.save(gstInvoiceHdrVO);
	}
}
