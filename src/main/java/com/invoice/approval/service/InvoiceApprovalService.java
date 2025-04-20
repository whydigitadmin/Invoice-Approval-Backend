package com.invoice.approval.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.entity.ExpenseUploadVO;
import com.invoice.approval.entity.GstInvoiceHdrVO;
import com.invoice.approval.entity.IRNQRVO;
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
	
	List<Map<String, Object>> getApprove1Db(String userName);
	
	List<Map<String, Object>> getApprove1TblDb(String userName);
	
	List<Map<String, Object>> getApprove1ChartDb(String userName);
	
	List<Map<String, Object>> getHaiCustomerRankDetails(String pName,String pType);
	
	List<Map<String, Object>> getHaiCustomerYearProfit(String pName,String pType);
	
	List<Map<String, Object>> getHaiCustomerDetails(String pName,String pType);
	
	List<Map<String, Object>> getHaiBranchCustomerDetails(String pName,String pType);
	
	List<Map<String, Object>> getHaiProductSummary(String product);
	
	List<Map<String, Object>> getHaiInvCustomerDetails(String pName,String pType);
	
	List<Map<String, Object>> getDayBookBranchWise(String branchName, String fromdate, String todate);
	
	List<Map<String, Object>> getAPAgeingInternal(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6);

	List<Map<String, Object>> getARAgeingInternal(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6);
	
	List<Map<String, Object>> getAROS(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6);
	
	List<Map<String, Object>> getAPOS(String sbcode,String div,String ptype,String pbranchname,String asondt,String slab1,String slab2,String slab3,String slab4,String slab5,String slab6);
	
    List<Map<String, Object>> getPartyLedger(String branchName,String sbcode,String fromdate,String todate,String subledgerType,String WithDet);
	
	List<Map<String, Object>> getPartyLedgerPartyName(String pType);
	
	List<Map<String, Object>> getLedgerAccountName();
	
	List<Map<String, Object>> getGSTR1Parties();
	
	List<Map<String, Object>> getGSTR1Filling(String branchName,String sbcode,String fromdate,String todate);
	
	List<Map<String, Object>> getProfitAndLoss(String branchName,String fromdate,String todate);
	
	List<Map<String, Object>> getTrailBalance(String branchName,String finyr,String fromdate,String todate,String WithDet);
	
	List<Map<String, Object>> getLedgerReport(String branchName,String accountName,String fromdate,String todate,String WithDet);
	
	List<Map<String, Object>> getAllOpenJobs(String branchName);
	
	List<Map<String, Object>> getJobUnApproveDetails(String jobNo);
	
	List<Map<String, Object>> getJobIncome(String jobNo);
	
	List<Map<String, Object>> getJobExpense(String jobNo);
	
	List<Map<String, Object>> getJobCloseddt(String jobNo,String closed);
	
    List<Map<String, Object>> getJobCostDetails(String branchName,String jobNo);
	
	List<Map<String, Object>> getJobCostSummary(String branchName,String jobNo);
	
	List<Map<String, Object>> getIRNDetails(String docNo);
	
	List<Map<String, Object>> getIRNDetailsList(String branchCode);
	
	List<Map<String, Object>> getIRNGridDetails(String docNo);
	
	IRNQRVO getIRNQRbyDocNo(String docNo);
	
	List<Map<String, Object>> getIRNJobDetails(String docNo);
	
	List<Map<String, Object>> getIRNJobInfo(String jobNo);
	
	List<Map<String, Object>> getIRNJobContDetails(String docNo);
	
	List<ExpenseUploadVO> ExcelUploadforExpense(MultipartFile file,String CreatedBy) throws ApplicationException, EncryptedDocumentException, IOException;

	int getTotalRows();

	int getSuccessfulUploads();
	
	

	
	
}
