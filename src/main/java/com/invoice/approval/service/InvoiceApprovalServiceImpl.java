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
			details=gstInvoiceHdrRepo.getPendingDetailsApprove1slab1(userName);
			
		}
		else if(userType.equals("approve2"))
		{
			details=gstInvoiceHdrRepo.getPendingDetailsApprove2slab1(userName);
			
		}
		else if(userType.equals("approve3"))
		{
			details=gstInvoiceHdrRepo.getPendingDetailsApprove1slab2(userName);
			
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
		else if(userType.equals("approve2"))
		{
			
			details=gstInvoiceHdrRepo.getInvDetailsApprove2(userName);
		}
		else if(userType.equals("approve3"))
		{
			details=gstInvoiceHdrRepo.getInvDetailsApprove3(userName);
			
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
			dtl.put("slabRemarks", det[11] != null ? det[11].toString() : "");
			dtl.put("exceedDays", det[12] != null ? det[12].toString() : "");
			dtl.put("eligiSlab", det[13] != null ? Integer.parseInt(det[13].toString()) : 0);
			dtl.put("unApproveAmt", det[14] != null ? df.format(new BigDecimal(det[14].toString())) : "");
			dtl.put("osBeyond", det[20] != null ? df.format(new BigDecimal(det[20].toString())) : "");
			dtl.put("excessCredit", det[21] != null ? df.format(new BigDecimal(det[21].toString())) : "");
			dtl.put("category", det[22] != null ? det[22].toString() : "");
			dtl.put("controllingOffice", det[23] != null ? det[23].toString() : "");
			dtl.put("salespersonName", det[24] != null ? det[24].toString() : "");
			
			
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
	public List<Map<String, Object>> getDayBookBranchWise(String branchName, String fromdate, String todate) {
		Set<Object[]>details= new HashSet<>();
		details=gstInvoiceHdrRepo.getDayBookBranchWise(branchName, fromdate, todate);
		return getDayBookBranchWise(details);
	}
	
	
	private List<Map<String, Object>> getDayBookBranchWise(Set<Object[]> details) {
		List<Map<String,Object>>report=new ArrayList<>();
		for(Object[]det:details)
		{
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			
			Map<String, Object> dtl= new HashMap<>();
			
			dtl.put("branchCode", det[0] != null ? det[0].toString() : "");
			dtl.put("vchNo", det[1] != null ? det[1].toString() : "");
			dtl.put("vchDate", det[2] != null ? det[2].toString() : "");
			dtl.put("docId", det[3] != null ? det[3].toString() : "");
			dtl.put("docDt", det[4] != null ? det[4].toString() : "");

			dtl.put("accountCode", det[5] != null ? det[5].toString() : "");
			dtl.put("ledger", det[6] != null ? det[6].toString() : "");
			dtl.put("subledgerCode", det[7] != null ? det[7].toString() : "");
			dtl.put("subledgerName", det[8] != null ? det[8].toString() : "");
			dtl.put("curr", det[9] != null ? det[9].toString() : "");
			
			dtl.put("exRate", det[10]  != null ? df.format(new BigDecimal(det[10].toString())) : "");
			dtl.put("bdbAmount", det[11] != null ? df.format(new BigDecimal(det[11].toString())) : "");
			dtl.put("bcrAmount", det[12] != null ? df.format(new BigDecimal(det[12].toString())) : "");
			dtl.put("remarks", det[13] != null ? det[13].toString() : "");
			
			
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
		}else if(userType.equals("approve2"))
		{ 
			if(approval.equals("1"))
			{
				gstInvoiceHdrVO.setApprove2("T");
				gstInvoiceHdrVO.setApprove2Name(createdby);
				gstInvoiceHdrVO.setApprove2On(LocalDateTime.now());
				gstInvoiceHdrVO.setInvProceed("T");
			}
			else {
				gstInvoiceHdrVO.setApprove2("F");
				gstInvoiceHdrVO.setApprove2Name(createdby);
				gstInvoiceHdrVO.setApprove2On(LocalDateTime.now());
				gstInvoiceHdrVO.setInvProceed("F");
			}
			
		}
		else
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
		    gstInvoiceHdrVO.setInvProceed(approval.equals("1") ? "T" : "F");
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


	@Override
	public List<Map<String, Object>> getUserBranch(String userName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Map<String, Object>> getMIS(String branchName,String status, String fromdate, String todate) {
		Set<Object[]>details= new HashSet<>();
		details=gstInvoiceHdrRepo.getMIS(branchName, status, fromdate, todate);
		return getMIS(details);
	}
	
	private List<Map<String, Object>> getMIS(Set<Object[]> details) {
	    List<Map<String, Object>> report = new ArrayList<>();
	    
	    for (Object[] det : details) {
	        // Create a DecimalFormat instance for formatting numbers
	        DecimalFormat df = new DecimalFormat("0.00");
	        // SimpleDateFormat instance if you plan to handle date formatting (though it's not used here)
	        
	        // Create a map to hold the details for each row
	        Map<String, Object> dtl = new HashMap<>();
	        
	        // Ensure the values are extracted based on the correct index in the Object[] (det)
	        dtl.put("jobBranch", det[0] != null ? det[0].toString() : "");
	        
	        // For other fields (income, expense, etc.), make sure you use the correct index for each one
	        dtl.put("income", det[1] != null ? df.format(new BigDecimal(det[1].toString())) : "");
	        dtl.put("expense", det[2] != null ? df.format(new BigDecimal(det[2].toString())) : "");
	        dtl.put("gp", det[3] != null ? df.format(new BigDecimal(det[3].toString())) : "");
	        dtl.put("branchGP", det[4] != null ? df.format(new BigDecimal(det[4].toString())) : "");
	        dtl.put("retainGP", det[5] != null ? df.format(new BigDecimal(det[5].toString())) : "");
	        dtl.put("issuedGP", det[6] != null ? df.format(new BigDecimal(det[6].toString())) : "");
	        dtl.put("receivedGP", det[7] != null ? df.format(new BigDecimal(det[7].toString())) : "");

	        // Add the map of details for the current row to the report list
	        report.add(dtl);
	    }
	    
	    return report;
	}

	

	public List<Map<String, Object>> getAllAPParties() {
		// TODO Auto-generated method stub
		 List<Map<String, Object>> report = new ArrayList<>();
		 Set<Object[]>details= new HashSet<>();
		 details=gstInvoiceHdrRepo.getAllAPParties();
		    
		    for (Object[] det : details) {
		        
		        DecimalFormat df = new DecimalFormat("0.00");
		        Map<String, Object> dtl = new HashMap<>();
		        
		        // Ensure the values are extracted based on the correct index in the Object[] (det)
		        dtl.put("subledgerCode", det[0] != null ? det[0].toString() : "");
		        dtl.put("subledgerName", det[1] != null ? det[1].toString() : "");
		        
		        report.add(dtl);
		    }
		    
		    return report;
	}

	
	public List<Map<String, Object>> getAllARParties() {
		// TODO Auto-generated method stub
		 List<Map<String, Object>> report = new ArrayList<>();
		 Set<Object[]>details= new HashSet<>();
		 details=gstInvoiceHdrRepo.getAllARParties();
		    
		    for (Object[] det : details) {
		        
		        DecimalFormat df = new DecimalFormat("0.00");
		        Map<String, Object> dtl = new HashMap<>();
		        
		        // Ensure the values are extracted based on the correct index in the Object[] (det)
		        dtl.put("subledgerCode", det[0] != null ? det[0].toString() : "");
		        dtl.put("subledgerName", det[1] != null ? det[1].toString() : "");
		        
		        report.add(dtl);
		    }
		    
		    return report;
	}
	
	public List<Map<String, Object>> getInvoices(String userName,String branchName) {
		// TODO Auto-generated method stub
		 List<Map<String, Object>> report = new ArrayList<>();
		 Set<Object[]>details= new HashSet<>();
		 details=gstInvoiceHdrRepo.getInvoices(userName,branchName);
		    
		    for (Object[] det : details) {
		        
		        DecimalFormat df = new DecimalFormat("0.00");
		        Map<String, Object> dtl = new HashMap<>();
		        
		        // Ensure the values are extracted based on the correct index in the Object[] (det)
		        dtl.put("docid", det[0] != null ? det[0].toString() : "");
		        dtl.put("docdt", det[1] != null ? det[1].toString() : "");
		        dtl.put("partyName", det[2] != null ? det[2].toString() : "");
		        dtl.put("partyCode", det[3] != null ? det[3].toString() : "");
		        dtl.put("vchno", det[4] != null ? det[4].toString() : "");
		        dtl.put("vchdt", det[5] != null ? det[5].toString() : "");
		        dtl.put("totinvamtLc", det[6] != null ? df.format(new BigDecimal(det[6].toString())) : "0.00");    
		        
		        report.add(dtl);
		    }
		    
		    return report;
	}
	
	


	
	@Override
	public List<Map<String, Object>> getAPAgeingInternal(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6) {
		Set<Object[]>details= new HashSet<>();
		details=gstInvoiceHdrRepo.getAPAgeingInternal(sbcode,div,ptype,pbranchname,asondt,slab1,slab2,slab3,slab4,slab5,slab6);
		return getAPAgeingInternal(details);
	}
	
	private List<Map<String, Object>> getAPAgeingInternal(Set<Object[]> details) {
	    List<Map<String, Object>> report = new ArrayList<>();
	    
	    for (Object[] det : details) {
	        // Create a DecimalFormat instance for formatting numbers
	        DecimalFormat df = new DecimalFormat("0.00");
	        // SimpleDateFormat instance if you plan to handle date formatting (though it's not used here)
	        
	        // Create a map to hold the details for each row
	        Map<String, Object> dtl = new HashMap<>();
	        
	        // Ensure the values are extracted based on the correct index in the Object[] (det)
	        dtl.put("branchName", det[2] != null ? det[2].toString() : "");
	        
	        dtl.put("subledgerCode", det[3] != null ? det[3].toString() : "");
	        dtl.put("subledgerName", det[4] != null ? det[4].toString() : "");
	        dtl.put("cbranch", det[6] != null ? det[6].toString() : "");
	        dtl.put("salesPersonName", det[7] != null ? det[7].toString() : "");
	        dtl.put("currency", det[9] != null ? det[9].toString() : "");
	        
	        dtl.put("docid", det[10] != null ? det[10].toString() : "");
	        dtl.put("docdt", det[11] != null ? det[11].toString() : "");
	        dtl.put("refNo", det[15] != null ? det[15].toString() : "");
	        dtl.put("refDate", det[16] != null ? det[16].toString() : "");
	        dtl.put("dueDate", det[14] != null ? det[14].toString() : "");
	        
	        dtl.put("amount", det[19] != null ? df.format(new BigDecimal(det[19].toString())) : "0.00");
	        dtl.put("outStanding", det[20] != null ? df.format(new BigDecimal(det[20].toString())) : "0.00");
	        dtl.put("totalDue", det[21] != null ? df.format(new BigDecimal(det[21].toString())) : "0.00");
	        dtl.put("unAdjusted", det[22] != null ? df.format(new BigDecimal(det[22].toString())) : "0.00");
	        dtl.put("mslab1", det[23] != null ? df.format(new BigDecimal(det[23].toString())) : "0.00");
	        dtl.put("mslab2", det[24] != null ? df.format(new BigDecimal(det[24].toString())) : "0.00");
	        dtl.put("mslab3", det[25] != null ? df.format(new BigDecimal(det[25].toString())) : "0.00");
	        dtl.put("mslab4", det[26] != null ? df.format(new BigDecimal(det[26].toString())) : "0.00");
	        dtl.put("mslab5", det[27] != null ? df.format(new BigDecimal(det[27].toString())) : "0.00");
	        dtl.put("mslab6", det[28] != null ? df.format(new BigDecimal(det[28].toString())) : "0.00");
	        dtl.put("mslab7", det[29] != null ? df.format(new BigDecimal(det[29].toString())) : "0.00");
	        
	        dtl.put("suppRefNo", det[12] != null ? det[12].toString() : "");
	        dtl.put("suppRefDate", det[13] != null ? det[13].toString() : "");
	        dtl.put("whRefNo", det[32] != null ? det[32].toString() : "");
	        dtl.put("mno", det[17] != null ? det[17].toString() : "");
	        dtl.put("hno", det[18] != null ? det[18].toString() : "");
	        
	        
	        

	        // Add the map of details for the current row to the report list
	        report.add(dtl);
	    }
	    
	    return report;
	}


	
	
	@Override
	public List<Map<String, Object>> getARAgeingInternal(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6) {
		Set<Object[]>details= new HashSet<>();
		details=gstInvoiceHdrRepo.getARAgeingInternal(sbcode,div,ptype,pbranchname,asondt,slab1,slab2,slab3,slab4,slab5,slab6);
		return getARAgeingInternal(details);
	}
	
	private List<Map<String, Object>> getARAgeingInternal(Set<Object[]> details) {
	    List<Map<String, Object>> report = new ArrayList<>();
	    
	    for (Object[] det : details) {
	        // Create a DecimalFormat instance for formatting numbers
	        DecimalFormat df = new DecimalFormat("0.00");
	        // SimpleDateFormat instance if you plan to handle date formatting (though it's not used here)
	        
	        // Create a map to hold the details for each row
	        Map<String, Object> dtl = new HashMap<>();
	        
	        // Ensure the values are extracted based on the correct index in the Object[] (det)
	        dtl.put("branchName", det[2] != null ? det[2].toString() : "");
	        
	        dtl.put("subledgerCode", det[3] != null ? det[3].toString() : "");
	        dtl.put("subledgerName", det[4] != null ? det[4].toString() : "");
	        dtl.put("cbranch", det[6] != null ? det[6].toString() : "");
	        dtl.put("salesPersonName", det[7] != null ? det[7].toString() : "");
	        dtl.put("currency", det[9] != null ? det[9].toString() : "");
	        
	        dtl.put("docid", det[10] != null ? det[10].toString() : "");
	        dtl.put("docdt", det[11] != null ? det[11].toString() : "");
	        dtl.put("refNo", det[15] != null ? det[15].toString() : "");
	        dtl.put("refDate", det[16] != null ? det[16].toString() : "");
	        dtl.put("dueDate", det[14] != null ? det[14].toString() : "");
	        
	        dtl.put("amount", det[19] != null ? df.format(new BigDecimal(det[19].toString())) : "0.00");
	        dtl.put("outStanding", det[20] != null ? df.format(new BigDecimal(det[20].toString())) : "0.00");
	        dtl.put("totalDue", det[21] != null ? df.format(new BigDecimal(det[21].toString())) : "0.00");
	        dtl.put("unAdjusted", det[22] != null ? df.format(new BigDecimal(det[22].toString())) : "0.00");
	        dtl.put("mslab1", det[23] != null ? df.format(new BigDecimal(det[23].toString())) : "0.00");
	        dtl.put("mslab2", det[24] != null ? df.format(new BigDecimal(det[24].toString())) : "0.00");
	        dtl.put("mslab3", det[25] != null ? df.format(new BigDecimal(det[25].toString())) : "0.00");
	        dtl.put("mslab4", det[26] != null ? df.format(new BigDecimal(det[26].toString())) : "0.00");
	        dtl.put("mslab5", det[27] != null ? df.format(new BigDecimal(det[27].toString())) : "0.00");
	        dtl.put("mslab6", det[28] != null ? df.format(new BigDecimal(det[28].toString())) : "0.00");
	        dtl.put("mslab7", det[29] != null ? df.format(new BigDecimal(det[29].toString())) : "0.00");
	        
	        dtl.put("suppRefNo", det[12] != null ? det[12].toString() : "");
	        dtl.put("suppRefDate", det[13] != null ? det[13].toString() : "");
	        dtl.put("whRefNo", det[32] != null ? det[32].toString() : "");
	        dtl.put("mno", det[17] != null ? det[17].toString() : "");
	        dtl.put("hno", det[18] != null ? det[18].toString() : "");
	        
	        
	        

	        // Add the map of details for the current row to the report list
	        report.add(dtl);
	    }
	    
	    return report;
	}



	
	@Override
	public List<Map<String, Object>> getAROS(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6) {
		Set<Object[]>details= new HashSet<>();
		details=gstInvoiceHdrRepo.getAROS(sbcode,div,ptype,pbranchname,asondt,slab1,slab2,slab3,slab4,slab5,slab6);
		return getAROS(details);
	}
	
	private List<Map<String, Object>> getAROS(Set<Object[]> details) {
	    List<Map<String, Object>> report = new ArrayList<>();
	    
	    for (Object[] det : details) {
	        // Create a DecimalFormat instance for formatting numbers
	        DecimalFormat df = new DecimalFormat("0.00");
	        // SimpleDateFormat instance if you plan to handle date formatting (though it's not used here)
	        
	        // Create a map to hold the details for each row
	        Map<String, Object> dtl = new HashMap<>();
	        
	        // Ensure the values are extracted based on the correct index in the Object[] (det)
	        
	        dtl.put("subledgerCode", det[0] != null ? det[0].toString() : "");
	        dtl.put("subledgerName", det[1] != null ? det[1].toString() : "");
	        dtl.put("partyType", det[2] != null ? det[2].toString() : "");
	        dtl.put("cbranch", det[3] != null ? det[3].toString() : "");
	        dtl.put("salesPersonName", det[4] != null ? det[4].toString() : "");
	        dtl.put("jobBranch", det[5] != null ? det[5].toString() : "");
	        dtl.put("currency", det[6] != null ? det[6].toString() : "");
	        dtl.put("creditDays", det[7] != null ? det[7].toString() : "");
	        dtl.put("creditLimit", det[8] != null ? det[8].toString() : "");
	      
	        
	        dtl.put("amount", det[9] != null ? df.format(new BigDecimal(det[9].toString())) : "0.00");
	        dtl.put("outStanding", det[10] != null ? df.format(new BigDecimal(det[10].toString())) : "0.00");
	        
	        dtl.put("unAdjusted", det[11] != null ? df.format(new BigDecimal(det[11].toString())) : "0.00");
	        dtl.put("totalDue", det[12] != null ? df.format(new BigDecimal(det[12].toString())) : "0.00");
	        dtl.put("mslab1", det[13] != null ? df.format(new BigDecimal(det[13].toString())) : "0.00");
	        dtl.put("mslab2", det[14] != null ? df.format(new BigDecimal(det[14].toString())) : "0.00");
	        dtl.put("mslab3", det[15] != null ? df.format(new BigDecimal(det[15].toString())) : "0.00");
	        dtl.put("mslab4", det[16] != null ? df.format(new BigDecimal(det[16].toString())) : "0.00");
	        dtl.put("mslab5", det[17] != null ? df.format(new BigDecimal(det[17].toString())) : "0.00");

	        

	        // Add the map of details for the current row to the report list
	        report.add(dtl);
	    }
	    
	    return report;
	}

	
	@Override
	public List<Map<String, Object>> getAPOS(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6) {
		Set<Object[]>details= new HashSet<>();
		details=gstInvoiceHdrRepo.getAPOS(sbcode,div,ptype,pbranchname,asondt,slab1,slab2,slab3,slab4,slab5,slab6);
		return getAPOS(details);
	}
	
	private List<Map<String, Object>> getAPOS(Set<Object[]> details) {
	    List<Map<String, Object>> report = new ArrayList<>();
	    
	    for (Object[] det : details) {
	        // Create a DecimalFormat instance for formatting numbers
	        DecimalFormat df = new DecimalFormat("0.00");
	        // SimpleDateFormat instance if you plan to handle date formatting (though it's not used here)
	        
	        // Create a map to hold the details for each row
	        Map<String, Object> dtl = new HashMap<>();
	        
	        // Ensure the values are extracted based on the correct index in the Object[] (det)
	        
	        dtl.put("subledgerCode", det[0] != null ? det[0].toString() : "");
	        dtl.put("subledgerName", det[1] != null ? det[1].toString() : "");
	        dtl.put("partyType", det[2] != null ? det[2].toString() : "");
	        dtl.put("cbranch", det[3] != null ? det[3].toString() : "");
	        dtl.put("salesPersonName", det[4] != null ? det[4].toString() : "");
	        
	        dtl.put("currency", det[5] != null ? det[5].toString() : "");
	        dtl.put("creditDays", det[6] != null ? det[6].toString() : "");
	        dtl.put("creditLimit", det[7] != null ? det[7].toString() : "");
	      
	        
	        dtl.put("amount", det[8] != null ? new BigDecimal(det[8].toString()) : BigDecimal.ZERO);
	        dtl.put("outStanding", det[9] != null ? new BigDecimal(det[9].toString()) : BigDecimal.ZERO);
	        dtl.put("unAdjusted", det[10] != null ? new BigDecimal(det[10].toString()) : BigDecimal.ZERO);
	        dtl.put("totalDue", det[11] != null ? new BigDecimal(det[11].toString()) : BigDecimal.ZERO);
	        dtl.put("mslab1", det[12] != null ? new BigDecimal(det[12].toString()) : BigDecimal.ZERO);
	        dtl.put("mslab2", det[13] != null ? new BigDecimal(det[13].toString()) : BigDecimal.ZERO);
	        dtl.put("mslab3", det[14] != null ? new BigDecimal(det[14].toString()) : BigDecimal.ZERO);
	        dtl.put("mslab4", det[15] != null ? new BigDecimal(det[15].toString()) : BigDecimal.ZERO);
	        dtl.put("mslab5", det[16] != null ? new BigDecimal(det[16].toString()) : BigDecimal.ZERO);
	        

	        // Add the map of details for the current row to the report list
	        report.add(dtl);
	    }
	    
	    return report;
	}


	
	public List<Map<String, Object>> getAllCreditParties() {
		// TODO Auto-generated method stub
		 List<Map<String, Object>> report = new ArrayList<>();
		 Set<Object[]>details= new HashSet<>();
		 details=gstInvoiceHdrRepo.getAllCreditParties();
		    
		    for (Object[] det : details) {
		        
		        DecimalFormat df = new DecimalFormat("0.00");
		        Map<String, Object> dtl = new HashMap<>();
		        
		        // Ensure the values are extracted based on the correct index in the Object[] (det)
		        dtl.put("mg_partyhdrid", det[0] != null ? det[0].toString() : "");
		        
		        // For other fields (income, expense, etc.), make sure you use the correct index for each one
		        dtl.put("partyName", det[1] != null ? det[1].toString() : "");
		        dtl.put("partyCode", det[2] != null ? det[2].toString() : "");
		        dtl.put("creditLimit", det[3] != null ? df.format(new BigDecimal(det[3].toString())) : "0.00");
		        dtl.put("creditDays", det[4] != null ? df.format(new BigDecimal(det[4].toString())) : "0.00");
		        dtl.put("category", det[5] != null ? det[5].toString() : "");
		        dtl.put("salesPersonName", det[6] != null ? det[6].toString() : "");
		        dtl.put("controllingOffice", det[7] != null ? det[7].toString() : "");
		        

		        // Add the map of details for the current row to the report list
		        report.add(dtl);
		    }
		    
		    return report;
	}


	@Override
	public List<Map<String, Object>> getPartyLedger(String branchName,String sbcode,String fromdate,String todate,String subledgerType,String WithDet) {
		Set<Object[]>details= new HashSet<>();
		details=gstInvoiceHdrRepo.getPartyLedger(branchName,sbcode,fromdate,todate,subledgerType,WithDet);
		return getPartyLedger(details);
	}
	
	private List<Map<String, Object>> getPartyLedger(Set<Object[]> details) {
	    List<Map<String, Object>> report = new ArrayList<>();
	    
	    for (Object[] det : details) {
	        // Create a DecimalFormat instance for formatting numbers
	        DecimalFormat df = new DecimalFormat("0.00");
	        // SimpleDateFormat instance if you plan to handle date formatting (though it's not used here)
	        
	        // Create a map to hold the details for each row
	        Map<String, Object> dtl = new HashMap<>();
	        
	        // Ensure the values are extracted based on the correct index in the Object[] (det)
	        dtl.put("sno", det[0] != null ? det[0].toString() : "");
	        dtl.put("docid", det[3] != null ? det[3].toString() : "");
	        
	        // For other fields (income, expense, etc.), make sure you use the correct index for each one
	        dtl.put("docDate", det[4] != null ? det[4].toString() : "");
	        dtl.put("refNo", det[5] != null ? det[5].toString() : "");
	        dtl.put("refDate", det[6] != null ? det[6].toString() : "");
	        dtl.put("suppRefNo", det[7] != null ? det[7].toString() : "");
	        dtl.put("suppRefDate", det[8] != null ? det[8].toString() : "");
	        dtl.put("subledgerCode", det[9] != null ? det[9].toString() : "");
	        dtl.put("subledgerName", det[10] != null ? det[10].toString() : "");

	        dtl.put("subledger", det[11] != null ? det[11].toString() : "");
	        dtl.put("currency", det[12] != null ? det[12].toString() : "");
	        dtl.put("opbal", det[13] != null ? df.format(new BigDecimal(det[13].toString())) : "");
	        dtl.put("dbAmount", det[14] != null ? df.format(new BigDecimal(det[14].toString())) : "");
	        dtl.put("crAmount", det[15] != null ? df.format(new BigDecimal(det[15].toString())) : "");
	        dtl.put("billDbAmount", det[16] != null ? df.format(new BigDecimal(det[16].toString())) : "");
	        dtl.put("billCrAmount", det[17] != null ? df.format(new BigDecimal(det[17].toString())) : "");
	        dtl.put("particulars", det[18] != null ? det[18].toString() : "");
	        // Add the map of details for the current row to the report list
	        report.add(dtl);
	    }
	    
	    return report;
	}

	@Override
	public List<Map<String, Object>> getPartyLedgerPartyName(String pType) {
		// TODO Auto-generated method stub
		 List<Map<String, Object>> report = new ArrayList<>();
		 Set<Object[]>details= new HashSet<>();
		 details=gstInvoiceHdrRepo.getPartyLedgerPartyName(pType);
		    
		    for (Object[] det : details) {
		        
		        DecimalFormat df = new DecimalFormat("0.00");
		        Map<String, Object> dtl = new HashMap<>();
		        
		        // Ensure the values are extracted based on the correct index in the Object[] (det)
		        
		        dtl.put("subledgerName", det[0] != null ? det[0].toString() : "");
		        
		        report.add(dtl);
		    }
		    
		    return report;
	}





	
}


