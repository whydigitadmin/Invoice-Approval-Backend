package com.invoice.approval.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.invoice.approval.entity.UTImportJob;

@Repository
public interface ImportJobRepo extends JpaRepository<UTImportJob, Long> {
    Optional<UTImportJob> findByReferenceId(String referenceId);
    Optional<UTImportJob> findByBeNo(String beNo);
}