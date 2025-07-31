package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.BranchVO;

public interface BranchRepo extends JpaRepository<BranchVO, Long> {
	
	BranchVO findByBranch(String branch);

}
