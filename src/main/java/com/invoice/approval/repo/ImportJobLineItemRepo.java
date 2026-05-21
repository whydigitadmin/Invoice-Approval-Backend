package com.invoice.approval.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.invoice.approval.entity.UTImportJobLineItem;

@Repository
public interface ImportJobLineItemRepo extends JpaRepository<UTImportJobLineItem, Long> {
    List<UTImportJobLineItem> findByInvoiceId(Long invoiceId);
    List<UTImportJobLineItem> findByJobId(Long jobId);
}