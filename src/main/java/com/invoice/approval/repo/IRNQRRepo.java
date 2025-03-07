package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.IRNQRVO;

public interface IRNQRRepo extends JpaRepository<IRNQRVO, String>{
    
	@Query(value="select a from IRNQRVO a where a.documentNumber = ?1")
	IRNQRVO getByDocid(String docNo);

	

}
