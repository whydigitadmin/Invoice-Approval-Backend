package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.DocTypeMappingDetailsVO;

public interface DocTypeMappingDetailsRepo extends JpaRepository<DocTypeMappingDetailsVO, Long> {

	DocTypeMappingDetailsVO findByBranchAndFinYearAndScreenCode(String branch, int finYear, String screenCode);

}

