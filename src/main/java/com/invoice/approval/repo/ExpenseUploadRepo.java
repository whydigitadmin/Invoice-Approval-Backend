package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.ExpenseUploadVO;

public interface ExpenseUploadRepo extends JpaRepository<ExpenseUploadVO, Long> {

	
	

}
