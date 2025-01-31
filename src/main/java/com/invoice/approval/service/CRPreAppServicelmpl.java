package com.invoice.approval.service;

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

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.CRPreAppDTO;
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
		if(userType.equals("approve3"))
		{
			details=crPreAppRepo.getCRPendingDetailsApprove1slab1(userName);
			
		}
		
		return pendingDetails(details);
	}

	
	@Override
	public List<Map<String, Object>> getApprovalReport(String userType,String userName) {
		
		Set<Object[]>details= new HashSet<>();
		if(userType.equals("approve3"))
		{
			
			details=crPreAppRepo.getCRApproveDetailsApprove1slab1(userName);
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
		crPreAppVO.setCrAmt(crPreAppDTO.getCrAmt());
		crPreAppVO.setReason(crPreAppDTO.getReason());
		crPreAppVO.setPtype(crPreAppDTO.getPtype());
		return crPreAppVO;
	}


	@Override
	public CRPreAppVO updateApprove1(Long id, String approval, String createdby,String userType) {
		CRPreAppVO crPreAppvo= crPreAppRepo.findByGSTPreCreditrId(id);
		if(userType.equals("approve3"))
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

}
