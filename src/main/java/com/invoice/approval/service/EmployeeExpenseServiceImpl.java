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

        // Map fields from DTO to VO
        mapEmployeeExpensesVOFromDTO(employeeExpensesVO, employeeExpensesDTO);

        // Save the entity
        employeeExpensesRepo.save(employeeExpensesVO);

        // Save associated expense attachments if any
        saveExpenseAttachments(employeeExpensesVO, employeeExpensesDTO);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("employeeExpensesVO", employeeExpensesVO);
        return response;
    }

    private void mapEmployeeExpensesVOFromDTO(EmployeeExpensesVO employeeExpensesVO, EmployeeExpensesDTO employeeExpensesDTO) {
        employeeExpensesVO.setEmpCode(employeeExpensesDTO.getEmpCode());
        employeeExpensesVO.setEmpName(employeeExpensesDTO.getEmpName());
        employeeExpensesVO.setTotamt(employeeExpensesDTO.getTotamt());
    }

    private void saveExpenseAttachments(EmployeeExpensesVO employeeExpensesVO, EmployeeExpensesDTO employeeExpensesDTO) throws IOException {
        List<EmployeeExpensesAttachmentVO> attachmentVOList = employeeExpensesDTO.getEmployeeExpensesAttachmentVO();

        if (attachmentVOList != null && !attachmentVOList.isEmpty()) {
            for (EmployeeExpensesAttachmentVO attachment : attachmentVOList) {
                attachment.setEmployeeExpensesVO(employeeExpensesVO);  // Set the relationship
                employeeExpensesAttachmentRepo.save(attachment);  // Save each attachment
            }
        }
    }

    @Override
    public void saveExpenseImages(List<MultipartFile> files, Long expenseId) throws IOException, ApplicationException {
        final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

        for (MultipartFile file : files) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ApplicationException("File size exceeds the maximum allowed limit of 5 MB: " + file.getOriginalFilename());
            }

            String fileName = file.getOriginalFilename();
            EmployeeExpensesVO employeeExpensesVO = employeeExpensesRepo.findById(expenseId)
                    .orElseThrow(() -> new EntityNotFoundException("Expense not found with ID: " + expenseId));
            EmployeeExpensesAttachmentVO attachmentVO = new EmployeeExpensesAttachmentVO();
            attachmentVO.setAttachment(file.getBytes());
            attachmentVO.setFileName(fileName);
            attachmentVO.setEmployeeExpensesVO(employeeExpensesVO);  // Link attachment to expense

            employeeExpensesAttachmentRepo.save(attachmentVO);
        }
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
