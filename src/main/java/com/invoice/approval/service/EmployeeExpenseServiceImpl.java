package com.invoice.approval.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.EmployeeExpensesDTO;
import com.invoice.approval.entity.EmployeeExpensesAttachmentVO;
import com.invoice.approval.entity.EmployeeExpensesVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.EmployeeExpensesAttachmentRepo;
import com.invoice.approval.repo.EmployeeExpensesRepo;

@Service
public class EmployeeExpenseServiceImpl implements EmployeeExpenseService {

	public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeExpenseServiceImpl.class);

	@Autowired
	EmployeeExpensesRepo employeeExpensesRepo;

	@Autowired
	EmployeeExpensesAttachmentRepo employeeExpensesAttachmentRepo;

	@Override
	public Map<String, Object> createUpdateEmployeeExpenses(EmployeeExpensesDTO employeeExpensesDTO)
			throws IOException {
		String message;
		EmployeeExpensesVO employeeExpensesVO = new EmployeeExpensesVO();

		// Check if ID is null for create or update operation
		if (ObjectUtils.isEmpty(employeeExpensesDTO.getId())) {
			// Create operation
			employeeExpensesVO.setCreatedBy(employeeExpensesDTO.getCreatedBy());
			employeeExpensesVO.setModifiedBy(employeeExpensesDTO.getCreatedBy());
			message = "Expense created successfully";
		} else {
			// Update operation
			employeeExpensesVO = employeeExpensesRepo.findById(employeeExpensesDTO.getId())
					.orElseThrow(() -> new EntityNotFoundException(
							"Employee expense not found with ID: " + employeeExpensesDTO.getId()));
			employeeExpensesVO.setModifiedBy(employeeExpensesDTO.getCreatedBy());
			message = "Expense updated successfully";
		}

		// Map fields from DTO to VO
		mapEmployeeExpensesVOFromDTO(employeeExpensesVO, employeeExpensesDTO);

		// Save the entity
		employeeExpensesRepo.save(employeeExpensesVO);

		// Prepare response
		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("employeeExpensesVO", employeeExpensesVO);
		return response;
	}

	private void mapEmployeeExpensesVOFromDTO(EmployeeExpensesVO employeeExpensesVO,
			EmployeeExpensesDTO employeeExpensesDTO) throws IOException {
		employeeExpensesVO.setExpenseCategory(employeeExpensesDTO.getExpenseCategory());
		employeeExpensesVO.setExpenseName(employeeExpensesDTO.getExpenseName());
		employeeExpensesVO.setExpenseDate(employeeExpensesDTO.getExpenseDate());
		employeeExpensesVO.setExpenseAmount(employeeExpensesDTO.getExpenseAmount());
		employeeExpensesVO.setEmpCode(employeeExpensesDTO.getEmpCode());
		employeeExpensesVO.setEmployee(employeeExpensesDTO.getEmployee());
	}

	@Override
	public void saveExpenseImages(List<MultipartFile> file, Long expenseId) throws IOException, ApplicationException {

		final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

		for (MultipartFile files : file) {
			// Check if the file size exceeds the limit
			if (files.getSize() > MAX_FILE_SIZE) {
				throw new ApplicationException(
						"File size exceeds the maximum allowed limit of 5 MB: " + files.getOriginalFilename());
			}
			String fileName = files.getOriginalFilename();
			EmployeeExpensesVO employeeExpensesVO = employeeExpensesRepo.findById(expenseId).get();
			EmployeeExpensesAttachmentVO employeeExpensesAttachmentVO = new EmployeeExpensesAttachmentVO();
			employeeExpensesAttachmentVO.setAttachment(files.getBytes());
			employeeExpensesAttachmentVO.setFileName(fileName);
			employeeExpensesAttachmentVO.setEmployeeExpensesVO(employeeExpensesVO);
			employeeExpensesAttachmentRepo.save(employeeExpensesAttachmentVO);
		}

	}

	@Override
	public EmployeeExpensesVO getEmployeeExpenseVOById(Long id) {
		EmployeeExpensesVO employeeExpensesVO= employeeExpensesRepo.findById(id).get();
		return employeeExpensesVO;
	}

	@Override
	public List<EmployeeExpensesVO> getAllEmployeeExpenseVO() {
		List<EmployeeExpensesVO> employeeExpensesVO= employeeExpensesRepo.findAll();
		return employeeExpensesVO;
	}

}
