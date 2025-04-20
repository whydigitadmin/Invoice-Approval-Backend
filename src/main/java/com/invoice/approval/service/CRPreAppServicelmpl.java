package com.invoice.approval.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.CRPreAppDTO;
import com.invoice.approval.entity.CRPreAppAttachmentVO;
import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.CRPreAppRepo;

@Service
public class CRPreAppServicelmpl  implements CRPreAppService{
	
	@Autowired
	CRPreAppRepo crPreAppRepo;
	
	@Override
	public Map<String, Object> updateCreateCRPreApp(CRPreAppDTO crPreAppDTO) throws ApplicationException {

		String message = null;

            CRPreAppVO  crPreAppVO= new CRPreAppVO();

		if (ObjectUtils.isEmpty(crPreAppDTO.getId())) {

			crPreAppVO = new CRPreAppVO();

			crPreAppVO.setCreatedBy(crPreAppDTO.getCreatedBy());
			crPreAppVO.setUpdatedBy(crPreAppDTO.getCreatedBy());

			message = "Credit Pre Request Creation Succesfully";

		} 

		crPreAppVO = getCRPreAppVOFromCRpreAppDTO(crPreAppVO, crPreAppDTO);
		crPreAppRepo.save(crPreAppVO);

		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("crPreAppVO", crPreAppVO);
		return response;

	}
	
	@Override
	public List<Map<String, Object>> getPendingApprovalReport(String userType,String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userType.equals("approve1"))
		{
			details=crPreAppRepo.getCRPendingDetailsApprove1slab1(userName);
			
		}
		if(userName.equals("admin"))
		{
			details=crPreAppRepo.getCRPendingDetailsApprove1slab1(userName);
			
		}
		
		return pendingDetails(details);
	}

	
	@Override
	public List<Map<String, Object>> getApprovalReport(String userType,String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userType.equals("approve1"))
		{
			
			details=crPreAppRepo.getCRApproveDetailsApprove1slab1(userName);
		}
		if(userName.equals("admin"))
		{
			details=crPreAppRepo.getCRApproveDetailsApprove1slab1(userName);
			
		}
		
		return approveDetails(details);
	}

	@Override
	public List<Map<String, Object>> getApprovalReport2(String userType,String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userType.equals("approve2"))
		{
			
			details=crPreAppRepo.getCRApproveDetailsApprove2slab1(userName);
		}
		
		if(userName.equals("admin"))
		{
			details=crPreAppRepo.getCRApproveDetailsApprove2slab1(userName);
			
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
			dtl.put("gst_precreditId",det[0]);
			dtl.put("branchName", det[1] != null ? det[1].toString() : "");
			dtl.put("crAmt", det[2] != null ? df.format(new BigDecimal(det[2].toString())) : "");
			dtl.put("crRemarks", det[3] != null ? det[3].toString() : "");
			dtl.put("invAmt", det[4] != null ? df.format(new BigDecimal(det[4].toString())) : "");
			dtl.put("partyCode", det[5] != null ? det[5].toString() : "");
			dtl.put("partyName", det[6] != null ? det[6].toString() : "");
			dtl.put("profoma", det[7] != null ? det[7].toString() : "");
			dtl.put("pType", det[8] != null ? det[8].toString() : "");
			dtl.put("reason", det[9] != null ? det[9].toString() : "");
			dtl.put("vchDt", det[10] != null ? dateFormat.format((Date) det[10]) : "");
			dtl.put("vchNo", det[11] != null ? det[11].toString() : "");
			dtl.put("osbcd", det[12] != null ? df.format(new BigDecimal(det[12].toString())) : "");
			dtl.put("totDue", det[13] != null ? df.format(new BigDecimal(det[13].toString())) : "");
			dtl.put("dDays", det[14] != null ? df.format(new BigDecimal(det[14].toString())) : "");
			dtl.put("category", det[15] != null ? det[15].toString() : "");
			dtl.put("controllingOffice", det[16] != null ? det[16].toString() : "");
			dtl.put("creditLimit", det[17] != null ? det[17].toString() : "");
			dtl.put("creditDays", det[18] != null ? det[18].toString() : "");
			dtl.put("salesPersonName", det[19] != null ? det[19].toString() : "");
			dtl.put("description", det[20] != null ? det[20].toString() : "");
			dtl.put("plImpact", det[21] != null ? det[21].toString() : "");
			dtl.put("documentsRequired", det[22] != null ? det[22].toString() : "");
			
			report.add(dtl);
		}
		return report;
	}
	
	
	private List<Map<String, Object>> approveDetails(Set<Object[]> details) {
		List<Map<String,Object>>report=new ArrayList<>();
		for(Object[]det:details)
		{
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl= new HashMap<>();
			dtl.put("gst_precreditId",det[0]);
			dtl.put("branchName", det[1] != null ? det[1].toString() : "");
			dtl.put("crAmt", det[2] != null ? df.format(new BigDecimal(det[2].toString())) : "");
			dtl.put("crRemarks", det[3] != null ? det[3].toString() : "");
			dtl.put("invAmt", det[4] != null ? df.format(new BigDecimal(det[4].toString())) : "");
			dtl.put("partyCode", det[5] != null ? det[5].toString() : "");
			dtl.put("partyName", det[6] != null ? det[6].toString() : "");
			dtl.put("profoma", det[7] != null ? det[7].toString() : "");
			dtl.put("pType", det[8] != null ? det[8].toString() : "");
			dtl.put("reason", det[9] != null ? det[9].toString() : "");
			dtl.put("vchDt", det[10] != null ? dateFormat.format((Date) det[10]) : "");
			dtl.put("vchNo", det[11] != null ? det[11].toString() : "");
			dtl.put("osbcd", det[12] != null ? df.format(new BigDecimal(det[12].toString())) : "");
			dtl.put("totDue", det[13] != null ? df.format(new BigDecimal(det[13].toString())) : "");
			dtl.put("dDays", det[14] != null ? df.format(new BigDecimal(det[14].toString())) : "");
			dtl.put("category", det[15] != null ? det[15].toString() : "");
			dtl.put("controllingOffice", det[16] != null ? det[16].toString() : "");
			dtl.put("creditLimit", det[17] != null ? det[17].toString() : "");
			dtl.put("creditDays", det[18] != null ? det[18].toString() : "");
			dtl.put("salesPersonName", det[19] != null ? det[19].toString() : "");
			dtl.put("description", det[20] != null ? det[20].toString() : "");
			dtl.put("plImpact", det[21] != null ? det[21].toString() : "");
			dtl.put("documentsRequired", det[22] != null ? det[22].toString() : "");
			
			report.add(dtl);
		}
		return report;
	}

	private CRPreAppVO getCRPreAppVOFromCRpreAppDTO(CRPreAppVO crPreAppVO, CRPreAppDTO crPreAppDTO) {

		crPreAppVO.setBranchName(crPreAppDTO.getBranchName());
		crPreAppVO.setProfoma(crPreAppDTO.getProfoma());

		crPreAppVO.setPartyName(crPreAppDTO.getPartyName());
		crPreAppVO.setPartyCode(crPreAppDTO.getPartyCode());
		crPreAppVO.setCrRemarks(crPreAppDTO.getCrRemarks());
		crPreAppVO.setVchNo(crPreAppDTO.getVchNo());
		crPreAppVO.setVchDt(crPreAppDTO.getVchDt());
		crPreAppVO.setInvAmt(crPreAppDTO.getInvAmt());
		crPreAppVO.setTotChargeAmtLc(crPreAppDTO.getTotChargeAmtLc());
		crPreAppVO.setTotTaxAmtLc(crPreAppDTO.getTotTaxAmtLc());
		crPreAppVO.setCrAmt(crPreAppDTO.getCrAmt());
		crPreAppVO.setReason(crPreAppDTO.getReason());
		crPreAppVO.setPtype(crPreAppDTO.getPtype());
		crPreAppVO.setDescription(crPreAppDTO.getDescription());
		crPreAppVO.setPlImpact(crPreAppDTO.getPlImpact());
		crPreAppVO.setDocumentsRequired(crPreAppDTO.getDocumentsRequired());
		return crPreAppVO;
	}


	@Override
	public CRPreAppVO updateApprove1(Long id, String approval, String createdby,String userType) {
		CRPreAppVO crPreAppvo= crPreAppRepo.findByGSTPreCreditrId(id);
		if(userType.equals("approve1"))
		{
			if(approval.equals("1"))
			{
				crPreAppvo.setApprove1("T");
				crPreAppvo.setApprove1Name(createdby);
				crPreAppvo.setApprove1On(LocalDateTime.now());
				
			}
			else {
				crPreAppvo.setApprove1("F");
				crPreAppvo.setApprove1Name(createdby);
				crPreAppvo.setApprove1On(LocalDateTime.now());
				
			}
		}
		
		return crPreAppRepo.save(crPreAppvo);
	}

	
	@Override
	public CRPreAppVO updateApprove2(Long id, String approval, String createdby,String userType) {
		CRPreAppVO crPreAppvo= crPreAppRepo.findByGSTPreCreditrId(id);
		if(userType.equals("approve2"))
		{
			if(approval.equals("2"))
			{
				crPreAppvo.setApprove2("T");
				crPreAppvo.setApprove2Name(createdby);
				crPreAppvo.setApprove2On(LocalDateTime.now());
				
			}
			else {
				crPreAppvo.setApprove2("F");
				crPreAppvo.setApprove2Name(createdby);
				crPreAppvo.setApprove2On(LocalDateTime.now());
				
			}
		}
		
		return crPreAppRepo.save(crPreAppvo);
	}

	
	@Override
	public List<Map<String, Object>> getPendingApprovalReport2(String userType,String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userType.equals("approve2"))
		{
			details=crPreAppRepo.getCRPendingDetailsApprove2slab1(userName);
			
		}
		if(userName.equals("admin"))
		{
			details=crPreAppRepo.getCRPendingDetailsApprove2slab1(userName);
			
		}
		return pendingDetails(details);
	}

	
	
	@Override
    public void saveUploadFiles(List<MultipartFile> file, Long id) throws ApplicationException, IOException {
        final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
        List<CRPreAppAttachmentVO> appAttachmentVO = new ArrayList<CRPreAppAttachmentVO>();
        // Fetch the expense and attachments
       
        CRPreAppVO crPreAppVO = crPreAppRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CN Approval not found with ID: " + id));

        for(MultipartFile files:file)
        {
        if (crPreAppVO ==null) {
            throw new ApplicationException("Mismatch between the number of files provided and the existing records in the database.");
        }

        // Iterate over both files and attachments
       

            // Update attachment details
            String fileName = files.getOriginalFilename();
            CRPreAppAttachmentVO appAttachmentVO1 = new CRPreAppAttachmentVO();
            
            appAttachmentVO1.setAttachment(files.getBytes());
            appAttachmentVO1.setCrPreAppVO(crPreAppVO);
            appAttachmentVO.add(appAttachmentVO1);
            
            
        // Save all updated attachments in bulk
            
        }
        
        crPreAppVO.setCrPreAppAttachmentVO(appAttachmentVO);
        
        crPreAppRepo.save(crPreAppVO);
        
    }

	@Override
	public CRPreAppVO getfindByGSTPreCreditrId(Long id) {
		// TODO Auto-generated method stub
		return crPreAppRepo.findByGSTPreCreditrId(id);
	}


	@Override
	public List<Map<String, Object>> getCRReasons() {
		Set<Object[]>details= new HashSet<>();
		details=crPreAppRepo.getCRReasons();
		return getCRReasons(details);
	}
	
	private List<Map<String, Object>> getCRReasons(Set<Object[]> details) {
		List<Map<String,Object>>report=new ArrayList<>();
		for(Object[]det:details)
		{
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl= new HashMap<>();
			dtl.put("gst_cnreasonId",det[0]);
			dtl.put("crReason", det[1] != null ? det[1].toString() : "");
			dtl.put("description", det[2] != null ? det[2].toString() : "");
			dtl.put("documentsRequired", det[3] != null ? det[3].toString() : "");
			dtl.put("plImpact", det[4] != null ? det[4].toString() : "");
			
			
			report.add(dtl);
		}
		return report;
	}

	
	  


}
