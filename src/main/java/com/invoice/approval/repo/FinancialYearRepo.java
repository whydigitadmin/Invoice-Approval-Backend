package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.FinancialYearVO;

public interface FinancialYearRepo extends JpaRepository<FinancialYearVO, Long> {

	FinancialYearVO findByFinYear(int finYear);

}
