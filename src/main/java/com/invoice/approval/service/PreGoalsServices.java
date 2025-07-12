package com.invoice.approval.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.PreGoalsDTO;
import com.invoice.approval.entity.PreGoalsVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface PreGoalsServices {

	// OMap<String, Object> createUpdatePreGoals(PreGoalsDtlDTO preGoalsDtlDTO)
	// throws IOException, ApplicationException;

	void saveExpenseImages(List<MultipartFile> file, Long expenseId) throws IOException, ApplicationException;

	PreGoalsVO getPreGoalsVOById(Long id);
	
	List<Map<String, Object>> getPreGoalsVOListById(Long id);
	
	List<Map<String, Object>> getPreGoalsbyreportingto(String reportingto);

	List<Map<String, Object>> getPreGoalsbyUserName(String userName);

	List<PreGoalsVO> getAllPreGoalsVO();

	Map<String, Object> createUpdatePreGoals(PreGoalsDTO preGoalsDTO) throws IOException, ApplicationException;

	Map<String, Object> approveUpdatePreGoals(PreGoalsDTO preGoalsDTO, String userName, String approve)
			throws IOException, ApplicationException;

	List<Map<String, Object>> getPreGoalsDtlbyid(Long id);

	PreGoalsVO updatePreGoalsApprovedDetails(Long id, String approve1, String approve1name);

}
