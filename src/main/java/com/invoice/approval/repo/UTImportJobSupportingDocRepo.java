package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTImportJobSupportingDoc;

@Repository
public interface UTImportJobSupportingDocRepo extends JpaRepository<UTImportJobSupportingDoc, Long> {
    List<UTImportJobSupportingDoc> findByJobId(Long jobId);
    List<UTImportJobSupportingDoc> findByInvoiceId(Long invoiceId);
    List<UTImportJobSupportingDoc> findByLineItemId(Long lineItemId);
    void deleteByJobId(Long jobId);
}