package com.invoice.approval.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTExportJob;

@Repository
public interface UTExportJobRepo extends JpaRepository<UTExportJob, Long> {
	 Optional<UTExportJob> findByJobId(Long jobId);
	    
	    // Or use referenceId
	    Optional<UTExportJob> findByReferenceId(String referenceId);
	    Optional<UTExportJob> findByJobNo(String jobNo);
	    
}