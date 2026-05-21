package com.invoice.approval.repo;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.invoice.approval.entity.UTImportJob;

@Repository
public interface UTImportJobRepo extends JpaRepository<UTImportJob, Long> {
    Optional<UTImportJob> findByReferenceId(String referenceId);
    List<UTImportJob> findByBeNo(String beNo);  // Changed from Optional to List if multiple results possible
    Optional<UTImportJob> findByJobNo(String jobNo);
}