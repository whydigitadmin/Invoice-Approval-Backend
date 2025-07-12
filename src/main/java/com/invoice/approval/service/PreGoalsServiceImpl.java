package com.invoice.approval.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.PreGoalsDTO;
import com.invoice.approval.dto.PreGoalsDtlDTO;
import com.invoice.approval.entity.PreGoalsDtlVO;
import com.invoice.approval.entity.PreGoalsVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.PreGoalsDtlRepo;
import com.invoice.approval.repo.PreGoalsRepo;

@Service
public class PreGoalsServiceImpl implements PreGoalsServices {

	public static final Logger LOGGER = LoggerFactory.getLogger(PreGoalsServiceImpl.class);

	@Autowired
	PreGoalsRepo preGoalsRepo;

	@Autowired
	PreGoalsDtlRepo preGoalsDtlRepo;

//	@Override
//	public Map<String, Object> createUpdatePreGoals(PreGoalsDtlDTO preGoalsDtlDTO)
//			throws IOException, ApplicationException {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	

	@Override  
	public Map<String, Object> createUpdatePreGoals(PreGoalsDTO preGoalsDTO)
			throws ApplicationException {

		String message;

		PreGoalsVO preGoalsVO = null;

		if (ObjectUtils.isEmpty(preGoalsDTO.getId())) {

			preGoalsVO = new PreGoalsVO();

			preGoalsVO.setCreatedBy(preGoalsDTO.getCreatedBy());

			message = "PreGoals Creation SuccessFully";

		} else {

			preGoalsVO = preGoalsRepo.findById(preGoalsDTO.getId())
					.orElseThrow(() -> new ApplicationException("PreGoals  not found with id: " + preGoalsDTO.getId()));

			preGoalsVO.setModifiedBy(preGoalsDTO.getCreatedBy());

			message = "PreGoals Updation SuccessFully";

		}

		preGoalsVO = getPreGoalsVOFormPreGoalsDTO(preGoalsVO, preGoalsDTO);
		preGoalsRepo.save(preGoalsVO);

		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("preGoalsVO", preGoalsVO);
		return response;
	}

	private PreGoalsVO getPreGoalsVOFormPreGoalsDTO(PreGoalsVO preGoalsVO, @Valid PreGoalsDTO preGoalsDTO) {

		preGoalsVO.setAppraisalYear(preGoalsDTO.getApprisalYear());
		preGoalsVO.setEmpCode(preGoalsDTO.getEmpCode());
		preGoalsVO.setEmpName(preGoalsDTO.getEmpName());
		preGoalsVO.setReportingto(preGoalsDTO.getReportingto());
		preGoalsVO.setReportingname(preGoalsDTO.getReportingname());
		if (ObjectUtils.isNotEmpty(preGoalsDTO.getId())) {
			List<PreGoalsDtlVO> goalsDetailsVOs = preGoalsDtlRepo.findByPreGoalsVO(preGoalsVO);
			preGoalsDtlRepo.deleteAll(goalsDetailsVOs);

		}

		List<PreGoalsDtlVO> preGoalsDetailsVOs = new ArrayList<>();
		for (PreGoalsDtlDTO preGoalsDetailsDTO : preGoalsDTO.getPreGoalsDtlDTO()) {
			PreGoalsDtlVO preGoalsDetailsVO = new PreGoalsDtlVO();

			preGoalsDetailsVO.setArea(preGoalsDetailsDTO.getArea());
			preGoalsDetailsVO.setGoals(preGoalsDetailsDTO.getGoals());
			preGoalsDetailsVO.setSelfinput(preGoalsDetailsDTO.getSelfinput());
			preGoalsDetailsVO.setRating(preGoalsDetailsDTO.getRating());
			preGoalsDetailsVO.setScore(preGoalsDetailsDTO.getScore());

			preGoalsDetailsVO.setPreGoalsVO(preGoalsVO);
			preGoalsDetailsVOs.add(preGoalsDetailsVO);

		}

		preGoalsVO.setPreGoalsDtlVO(preGoalsDetailsVOs);

		return preGoalsVO;
	}

	@Override
	public Map<String, Object> approveUpdatePreGoals(@Valid PreGoalsDTO preGoalsDTO, String userName, String approve)
			throws ApplicationException {

		String message;

		PreGoalsVO preGoalsVO = null;

		if (ObjectUtils.isEmpty(preGoalsDTO.getId())) {

			preGoalsVO = new PreGoalsVO();

			preGoalsVO.setCreatedBy(preGoalsDTO.getCreatedBy());

			message = "PreGoals Creation SuccessFully";

		} else {

			preGoalsVO = preGoalsRepo.findById(preGoalsDTO.getId())
					.orElseThrow(() -> new ApplicationException("PreGoals  not found with id: " + preGoalsDTO.getId()));

			preGoalsVO.setModifiedBy(preGoalsDTO.getCreatedBy());

			message = "PreGoals Updation SuccessFully";

			message = "PreGoals Updation SuccessFully";
		}

		preGoalsVO = getPreGoalsVOFormPreGoalsDTO(preGoalsVO, preGoalsDTO, userName, approve);
		preGoalsRepo.save(preGoalsVO);

		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("preGoalsVO", preGoalsVO);
		return response;
	}

	private PreGoalsVO getPreGoalsVOFormPreGoalsDTO(PreGoalsVO preGoalsVO, @Valid PreGoalsDTO preGoalsDTO,
			String userName, String approve) {

		// wherever you decide to update the approval…
		preGoalsVO.setApprove1(approve); // or "F"

		// now only set the name/on if it really was approved

		preGoalsVO.setApprove1name(userName);
		preGoalsVO.setApprove1on(LocalDateTime.now());
		preGoalsVO.setEmpCode(preGoalsDTO.getEmpCode());
		preGoalsVO.setEmpName(preGoalsDTO.getEmpName());

		if (ObjectUtils.isNotEmpty(preGoalsDTO.getId())) {
			List<PreGoalsDtlVO> goalsDetailsVOs = preGoalsDtlRepo.findByPreGoalsVO(preGoalsVO);
			preGoalsDtlRepo.deleteAll(goalsDetailsVOs);

		}

		List<PreGoalsDtlVO> preGoalsDetailsVOs = new ArrayList<>();
		for (PreGoalsDtlDTO preGoalsDetailsDTO : preGoalsDTO.getPreGoalsDtlDTO()) {
			PreGoalsDtlVO preGoalsDetailsVO = new PreGoalsDtlVO();

			preGoalsDetailsVO.setArea(preGoalsDetailsDTO.getArea());
			preGoalsDetailsVO.setGoals(preGoalsDetailsDTO.getGoals());
			preGoalsDetailsVO.setSelfinput(preGoalsDetailsDTO.getSelfinput());
			preGoalsDetailsVO.setRating(preGoalsDetailsDTO.getRating());
			preGoalsDetailsVO.setScore(preGoalsDetailsDTO.getScore());

			preGoalsDetailsVO.setPreGoalsVO(preGoalsVO);
			preGoalsDetailsVOs.add(preGoalsDetailsVO);

		}

		preGoalsVO.setPreGoalsDtlVO(preGoalsDetailsVOs);

		return preGoalsVO;
	}
	
	@Override
	public PreGoalsVO getPreGoalsVOById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Map<String, Object>> getPreGoalsVOListById(Long id) {
		Set<Object[]> details = preGoalsRepo.getPreGoalsVOListById(id);
		return getPreGoalsVOListById(details);
	}

	private List<Map<String, Object>> getPreGoalsVOListById(Set<Object[]> details) {
		List<Map<String, Object>> report = new ArrayList<>();
		for (Object[] det : details) {
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl = new HashMap<>();
			dtl.put("gst_pregoalsid", det[0]);
			dtl.put("appraisalYear", det[1] != null ? det[1].toString() : "");
			dtl.put("empcode", det[2] != null ? det[2].toString() : "");
			dtl.put("empname", det[3] != null ? det[3].toString() : "");
			dtl.put("reportingto", det[4] != null ? det[4].toString() : "");
			dtl.put("reportingname", det[5] != null ? det[5].toString() : "");
			dtl.put("approve1", det[6] != null ? det[6].toString() : "");
			report.add(dtl);
		}
		return report;
	}

	@Override
	public List<PreGoalsVO> getAllPreGoalsVO() {
		return preGoalsRepo.findAll();
	}

	

	@Override
	public void saveExpenseImages(List<MultipartFile> file, Long expenseId) throws IOException, ApplicationException {
		// TODO Auto-generated method stub

	}
	
	
	@Override
	public List<Map<String, Object>> getPreGoalsbyreportingto(String reportingto) {
		Set<Object[]> details = preGoalsRepo.getPreGoalsbyreportingto(reportingto);
		return getPreGoalsbyreportingto(details);
	}

	private List<Map<String, Object>> getPreGoalsbyreportingto(Set<Object[]> details) {
		List<Map<String, Object>> report = new ArrayList<>();
		for (Object[] det : details) {
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl = new HashMap<>();
			dtl.put("gst_pregoalsid", det[0]);
			dtl.put("appraisalYear", det[1] != null ? det[1].toString() : "");
			dtl.put("empcode", det[2] != null ? det[2].toString() : "");
			dtl.put("empname", det[3] != null ? det[3].toString() : "");
			dtl.put("reportingto", det[4] != null ? det[4].toString() : "");
			dtl.put("reportingname", det[5] != null ? det[5].toString() : "");
			
			report.add(dtl);
		}
		return report;
	}


	@Override
	public List<Map<String, Object>> getPreGoalsbyUserName(String userName) {
		Set<Object[]> details = preGoalsRepo.getPreGoalsbyUserName(userName);
		return getPreGoalsbyUserName(details);
	}

	private List<Map<String, Object>> getPreGoalsbyUserName(Set<Object[]> details) {
		List<Map<String, Object>> report = new ArrayList<>();
		for (Object[] det : details) {
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl = new HashMap<>();
			dtl.put("gst_pregoalsid", det[0]);
			dtl.put("appraisalYear", det[1] != null ? det[1].toString() : "");
			dtl.put("empcode", det[2] != null ? det[2].toString() : "");
			dtl.put("empname", det[3] != null ? det[3].toString() : "");
			
			report.add(dtl);
		}
		return report;
	}

	@Override
	public List<Map<String, Object>> getPreGoalsDtlbyid(Long id) {
		Set<Object[]> details = new HashSet<>();
		details = preGoalsRepo.getPreGoalsDtlbyid(id);
		return getPreGoalsDtlbyid(details);
	}

	private List<Map<String, Object>> getPreGoalsDtlbyid(Set<Object[]> details) {
		List<Map<String, Object>> report = new ArrayList<>();
		for (Object[] det : details) {
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl = new HashMap<>();
			dtl.put("gst_pregoalsid", det[0]);
			dtl.put("area", det[1] != null ? det[1].toString() : "");
			dtl.put("goal", det[2] != null ? det[2].toString() : "");
			dtl.put("selfinput", det[3] != null ? det[3].toString() : "");
			dtl.put("rating", det[4] != null ? det[4].toString() : "");
			dtl.put("score", det[5] != null ? new BigDecimal(det[5].toString()) : BigDecimal.ZERO);
			

			report.add(dtl);
		}
		return report;
	}

	

	@Override
	public PreGoalsVO updatePreGoalsApprovedDetails(Long id, String approve1,String approve1name) {
		
		PreGoalsVO preGoalsVO = preGoalsRepo.findPregoals(id);

		if (preGoalsVO == null) {
			throw new RuntimeException("PrGoals Records not found This Id");
		}

		preGoalsVO.setApprove1(approve1);
		preGoalsVO.setApprove1name(approve1name);
		preGoalsVO.setApprove1on(LocalDateTime.now());

	

		return preGoalsRepo.save(preGoalsVO);

	}



//	@Override
//	public Map<String, Object> createUpdatePreGoals(PreGoalsDtlDTO preGoalsDtlDTO)
//			throws IOException, ApplicationException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	

	
	
}
