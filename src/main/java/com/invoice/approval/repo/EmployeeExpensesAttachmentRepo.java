package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.EmployeeExpensesAttachmentVO;

public interface EmployeeExpensesAttachmentRepo  extends JpaRepository<EmployeeExpensesAttachmentVO, Long>{

}
