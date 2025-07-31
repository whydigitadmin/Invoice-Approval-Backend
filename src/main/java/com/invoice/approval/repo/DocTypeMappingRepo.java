package com.invoice.approval.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.DocTypeMappingVO;

public interface DocTypeMappingRepo extends JpaRepository<DocTypeMappingVO, Long> {

	

	
	@Query(nativeQuery = true, value = "select ?1 branch,?2 branchCode,?3 finyear,?4 finyearid, doccode,screencode,screenname from documenttype  where screencode not in(\r\n"
			+ "select screencode from documenttypemappingdetails where branch=?1 and finyear=?3 group by screencode)")
	Set<Object[]> getPendingDocTypeMappingDetails(String branch, String branchCode, int finYear, int finYearId);
	
}
