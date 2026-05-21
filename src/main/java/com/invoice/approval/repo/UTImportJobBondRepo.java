package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTImportJobBond;

@Repository
public interface UTImportJobBondRepo extends JpaRepository<UTImportJobBond, Long> {
    List<UTImportJobBond> findByJobId(Long jobId);
    void deleteByJobId(Long jobId);
}