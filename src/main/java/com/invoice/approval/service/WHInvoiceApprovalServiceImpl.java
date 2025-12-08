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


import com.invoice.approval.entity.WHInvoiceHdrVO;
import com.invoice.approval.repo.WHInvoiceHdrRepo;

@Service
public class WHInvoiceApprovalServiceImpl implements WHInvoiceApprovalService {

	public static final Logger LOGGER = LoggerFactory.getLogger(InvoiceApprovalServiceImpl.class);
	
	@Autowired
	WHInvoiceHdrRepo whInvoiceHdrRepo;
	
	@Override
	public List<Map<String, Object>> getPendingApprovalReport(String userType,String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userType.equals("approve1"))
		{
			details=whInvoiceHdrRepo.getPendingDetailsApprove1slab1(userName);
			
		}
		else if(userType.equals("approve2"))
		{
			details=whInvoiceHdrRepo.getPendingDetailsApprove1slab2(userName);
			
		}
	
		
		return pendingDetails(details);
	}
	
	
	@Override
	public List<Map<String, Object>> getAdminPendingDetailsApprove1slab(String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userName.equals("admin"))
		{
			details=whInvoiceHdrRepo.getAdminPendingDetailsApprove1slab(userName);
			
		}

	
		
		return pendingDetails(details);
	}
	
	@Override
	public List<Map<String, Object>> getApprovalReport(String userType,String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userType.equals("approve1"))
		{
			
			details=whInvoiceHdrRepo.getWHInvDetailsApprove1(userName);
		}
		else if(userType.equals("approve2"))
		{
			
			details=whInvoiceHdrRepo.getPendingDetailsApprove1slab2(userName);
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
			dtl.put("WHInvoiceHdrId",det[0]);
			dtl.put("branchCode", det[1] != null ? det[1].toString() : "");
			dtl.put("finYear", det[2] != null ? det[2].toString() : "");
			dtl.put("docId", det[3] != null ? det[3].toString() : "");
			dtl.put("docDate", det[4] != null ? dateFormat.format((Date) det[4]) : "");
			dtl.put("partyName", det[5] != null ? det[5].toString() : "");
			dtl.put("partyCode", det[6] != null ? det[6].toString() : "");
			dtl.put("outStanding", det[7] != null ? df.format(new BigDecimal(det[7].toString())) : "");
			dtl.put("totalInvAmtLc", det[8] != null ? df.format(new BigDecimal(det[8].toString())) : "");
			dtl.put("creditDays", det[0] != null ? df.format(new BigDecimal(det[9].toString())) : "");
			dtl.put("creditLimit", det[10] != null ? df.format(new BigDecimal(det[10].toString())) : "");
			dtl.put("slabRemarks", det[11] != null ? det[11].toString() : "");
			dtl.put("exceedDays", det[12] != null ? det[12].toString() : "");
			dtl.put("eligiSlab", det[13] != null ? df.format(new BigDecimal(det[13].toString())) : "");
			dtl.put("unApproveAmt", det[14] != null ? df.format(new BigDecimal(det[14].toString())) : "");
			dtl.put("osBeyond", det[15] != null ? df.format(new BigDecimal(det[15].toString())) : "");
			dtl.put("excessCredit", det[16] != null ? df.format(new BigDecimal(det[16].toString())) : "");
			dtl.put("category", det[17] != null ? det[17].toString() : "");
			dtl.put("controllingOffice", det[18] != null ? det[18].toString() : "");
			dtl.put("salespersonName", det[19] != null ? det[19].toString() : "");
			
			
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
			dtl.put("WHInvoiceHdrId",det[0]);
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
			dtl.put("approve1on", det[16] != null ? det[16].toString() : "");
			dtl.put("approve2on", det[18] != null ? det[18].toString() : "");
			dtl.put("approve3on", det[19] != null ? det[19].toString() : "");
			dtl.put("osBeyond", det[20] != null ? df.format(new BigDecimal(det[20].toString())) : "");
			dtl.put("excessCredit", det[21] != null ? df.format(new BigDecimal(det[21].toString())) : "");
			dtl.put("category", det[22] != null ? det[22].toString() : "");
			dtl.put("controllingOffice", det[23] != null ? det[23].toString() : "");
			dtl.put("salespersonName", det[24] != null ? det[24].toString() : "");
			dtl.put("slabRemarks", det[25] != null ? det[25].toString() : "");
			
			
			report.add(dtl);
		}
		return report;
	}

	@Override
	public List<Map<String, Object>> getUserBranch(String userName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public WHInvoiceHdrVO updateApprove1(Long id, String approval, String createdby,String userType) {
		WHInvoiceHdrVO whInvoiceHdrVO= whInvoiceHdrRepo.findByWHInvoiceHdrId(id);
		if(userType.equals("approve1"))
		{
			if(approval.equals("1"))
			{
				whInvoiceHdrVO.setApprove1("T");
				whInvoiceHdrVO.setApprove1Name(createdby);
				whInvoiceHdrVO.setApprove1On(LocalDateTime.now());
				
			}
			else {
				whInvoiceHdrVO.setApprove1("F");
				whInvoiceHdrVO.setApprove1Name(createdby);
				whInvoiceHdrVO.setApprove1On(LocalDateTime.now());
				
			}
		}
			
			return whInvoiceHdrRepo.save(whInvoiceHdrVO);
		}
		
	
	
	


}