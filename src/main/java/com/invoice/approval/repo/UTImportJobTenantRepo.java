package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTImportJobTenant;

@Repository
public interface UTImportJobTenantRepo extends JpaRepository<UTImportJobTenant, Long> {
    List<UTImportJobTenant> findByJobId(Long jobId);
    void deleteByJobId(Long jobId);
}