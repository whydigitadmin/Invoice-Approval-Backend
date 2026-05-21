package com.invoice.approval.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTImportJobInvoice;

@Repository
public interface ImportJobInvoiceRepo extends JpaRepository<UTImportJobInvoice, Long> {
    List<UTImportJobInvoice> findByJobId(Long jobId);
    Optional<UTImportJobInvoice> findByInvoiceNo(String invoiceNo);
}