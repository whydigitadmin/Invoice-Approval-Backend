package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UTImportJobHss;

@Repository
public interface UTImportJobHssRepo extends JpaRepository<UTImportJobHss, Long> {
    List<UTImportJobHss> findByJobId(Long jobId);
    void deleteByJobId(Long jobId);
}