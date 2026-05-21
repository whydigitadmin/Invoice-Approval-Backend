package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTImportJobCertificate;

@Repository
public interface UTImportJobCertificateRepo extends JpaRepository<UTImportJobCertificate, Long> {
    List<UTImportJobCertificate> findByJobId(Long jobId);
    void deleteByJobId(Long jobId);
}