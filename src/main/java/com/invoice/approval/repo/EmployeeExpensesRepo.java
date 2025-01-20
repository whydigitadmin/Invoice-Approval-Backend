package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.EmployeeExpensesVO;

public interface EmployeeExpensesRepo extends JpaRepository<EmployeeExpensesVO, Long> {

}
