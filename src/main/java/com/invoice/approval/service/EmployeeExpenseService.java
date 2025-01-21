package com.invoice.approval.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.EmployeeExpensesDTO;
import com.invoice.approval.entity.EmployeeExpensesVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface EmployeeExpenseService {
	

	Map<String, Object> createUpdateEmployeeExpenses(EmployeeExpensesDTO employeeExpensesDTO) throws IOException, ApplicationException;

	void saveExpenseImages(List<MultipartFile> file, Long expenseId) throws IOException, ApplicationException;
	
	EmployeeExpensesVO getEmployeeExpenseVOById(Long id);

	List<EmployeeExpensesVO> getAllEmployeeExpenseVO();

}
