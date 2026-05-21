package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTImportJobStatement;

@Repository
public interface UTImportJobStatementRepo extends JpaRepository<UTImportJobStatement, Long> {
    List<UTImportJobStatement> findByJobId(Long jobId);
    List<UTImportJobStatement> findByInvoiceId(Long invoiceId);
    void deleteByJobId(Long jobId);
}