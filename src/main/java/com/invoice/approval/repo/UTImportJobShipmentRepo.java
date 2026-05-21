package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTImportJobShipment;

@Repository
public interface UTImportJobShipmentRepo extends JpaRepository<UTImportJobShipment, Long> {
    List<UTImportJobShipment> findByJobId(Long jobId);
    void deleteByJobId(Long jobId);
}