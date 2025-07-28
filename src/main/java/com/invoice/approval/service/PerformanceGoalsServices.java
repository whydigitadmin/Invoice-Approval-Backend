package com.invoice.approval.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.PerformanceGoalsDTO;
import com.invoice.approval.entity.POVO;
import com.invoice.approval.entity.PerformanceGoalsVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface PerformanceGoalsServices {

	// OMap<String, Object> createUpdatePreGoals(PreGoalsDtlDTO preGoalsDtlDTO)
	// throws IOException, ApplicationException;

	void saveExpenseImages(List<MultipartFile> file, Long expenseId) throws IOException, ApplicationException;
	
	List<PerformanceGoalsVO> findAll();

	PerformanceGoalsVO getPerformanceGoalsVOById(Long id);
	
	List<Map<String, Object>> getPerformanceGoalsVOListById(Long id);
	
	List<Map<String, Object>> getPerformanceGoalsbyreportingto(String reportingto);

	List<Map<String, Object>> getPerformanceGoalsbyUserName(String userName);
	
	List<Map<String, Object>> getReportingUserName(String userName);

	List<PerformanceGoalsVO> getAllPerformanceGoalsVO();

	Map<String, Object> createUpdatePerformanceGoals(PerformanceGoalsDTO performanceGoalsDTO) throws IOException, ApplicationException;

	Map<String, Object> approveUpdatePreGoals(PerformanceGoalsDTO performanceGoalsDTO, String userName, String approve)
			throws IOException, ApplicationException;

	List<Map<String, Object>> getPerformanceGoalsDtlbyid(Long id);

	PerformanceGoalsVO updatePerformanceGoalsApprovedDetails(Long id, String approve1, String approve1name);

	

}
