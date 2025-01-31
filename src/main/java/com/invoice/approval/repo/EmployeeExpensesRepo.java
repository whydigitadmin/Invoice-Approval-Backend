package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.invoice.approval.entity.EmployeeExpensesVO;

public interface EmployeeExpensesRepo extends JpaRepository<EmployeeExpensesVO, Long> {
	
	@Query(value = "select a from EmployeeExpensesVO a where a.id=?1")
	EmployeeExpensesVO findByExpEmpId(Long id);
	
	 @Query("SELECT e FROM EmployeeExpensesVO e WHERE e.approve1 = 'F'")
	    List<EmployeeExpensesVO> findAll();

	

}
