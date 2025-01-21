package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.EmployeeExpensesAttachmentVO;
import com.invoice.approval.entity.EmployeeExpensesVO;

public interface EmployeeExpensesAttachmentRepo  extends JpaRepository<EmployeeExpensesAttachmentVO, Long>{


	List<EmployeeExpensesAttachmentVO> findByEmployeeExpensesVO(EmployeeExpensesVO employeeExpensesV1);

}
