package com.invoice.approval.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
    public Map<String, Object> createUpdateEmployeeExpenses(EmployeeExpensesDTO employeeExpensesDTO) throws IOException {
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
                    .orElseThrow(() -> new EntityNotFoundException("Employee expense not found with ID: " + employeeExpensesDTO.getId()));
            employeeExpensesVO.setModifiedBy(employeeExpensesDTO.getCreatedBy());
            message = "Expense updated successfully";
        }

        saveExpenseAttachments(employeeExpensesVO, employeeExpensesDTO);
        // Map fields from DTO to VO
        employeeExpensesRepo.save(employeeExpensesVO);

        // Save associated expense attachments if any
        

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("employeeExpensesVO", employeeExpensesVO);
        return response;
    }

    private void saveExpenseAttachments(EmployeeExpensesVO employeeExpensesVO, EmployeeExpensesDTO employeeExpensesDTO) {
    	AtomicReference<BigDecimal> totAmount = new AtomicReference<>(BigDecimal.ZERO);
        employeeExpensesVO.setEmpCode(employeeExpensesDTO.getEmpCode());
        employeeExpensesVO.setEmpName(employeeExpensesDTO.getEmpName());
        List<EmployeeExpensesAttachmentVO>employeeExpensesAttachmentVOs=employeeExpensesDTO.getEmployeeExpensesAttachmentDTO()
        		.stream()
        		.map(employeeAttachmentDTO -> {
        			EmployeeExpensesAttachmentVO employeeExpensesAttachmentVO= new EmployeeExpensesAttachmentVO();
        			employeeExpensesAttachmentVO.setCategory(employeeAttachmentDTO.getCategory());
        			employeeExpensesAttachmentVO.setEmpCode(employeeExpensesDTO.getEmpCode());
        			employeeExpensesAttachmentVO.setEmpName(employeeExpensesDTO.getEmpName());
        			employeeExpensesAttachmentVO.setExpDate(employeeAttachmentDTO.getExpDate());
        			employeeExpensesAttachmentVO.setExpense(employeeAttachmentDTO.getExpense());
        			employeeExpensesAttachmentVO.setAmount(employeeAttachmentDTO.getAmount());
        			employeeExpensesAttachmentVO.setEmployeeExpensesVO(employeeExpensesVO);
        			totAmount.set(totAmount.get().add(employeeAttachmentDTO.getAmount()));
        			return employeeExpensesAttachmentVO;
        		}).collect(Collectors.toList());
        employeeExpensesVO.setTotamt(totAmount.get());
        employeeExpensesVO.setEmployeeExpensesAttachmentVO(employeeExpensesAttachmentVOs);
    }

    @Override
    public void saveExpenseImages(List<MultipartFile> files, Long expenseId) throws IOException, ApplicationException {
        final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

        // Fetch the expense and attachments
        EmployeeExpensesVO employeeExpensesVO = employeeExpensesRepo.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with ID: " + expenseId));

        List<EmployeeExpensesAttachmentVO> attachmentVOs = employeeExpensesAttachmentRepo.findByEmployeeExpensesVO(employeeExpensesVO);

        if (files.size() != attachmentVOs.size()) {
            throw new ApplicationException("Mismatch between the number of files provided and the existing records in the database.");
        }

        // Iterate over both files and attachments
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            EmployeeExpensesAttachmentVO attachmentVO = attachmentVOs.get(i);

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ApplicationException("File size exceeds the maximum allowed limit of 5 MB: " + file.getOriginalFilename());
            }

            // Update attachment details
            String fileName = file.getOriginalFilename();
            attachmentVO.setFileName(fileName);
            attachmentVO.setAttachment(file.getBytes());
            attachmentVO.setEmployeeExpensesVO(employeeExpensesVO); // Link attachment to expense
        }

        // Save all updated attachments in bulk
        employeeExpensesAttachmentRepo.saveAll(attachmentVOs);
    }


    @Override
    public EmployeeExpensesVO getEmployeeExpenseVOById(Long id) {
        return employeeExpensesRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee expense not found with ID: " + id));
    }

    @Override
    public List<EmployeeExpensesVO> getAllEmployeeExpenseVO() {
        return employeeExpensesRepo.findAll();
    }
}
