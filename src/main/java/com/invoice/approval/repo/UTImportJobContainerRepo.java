package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTImportJobContainer;

@Repository
public interface UTImportJobContainerRepo extends JpaRepository<UTImportJobContainer, Long> {
    List<UTImportJobContainer> findByShipmentId(Long shipmentId);
    List<UTImportJobContainer> findByJobId(Long jobId);
    void deleteByShipmentId(Long shipmentId);
}