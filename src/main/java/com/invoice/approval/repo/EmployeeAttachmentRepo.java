package com.invoice.approval.repo;

import com.invoice.approval.entity.EmployeeAttachmentVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeAttachmentRepo extends JpaRepository<EmployeeAttachmentVO, Long> {
    
    // For single result (if email is unique)
    Optional<EmployeeAttachmentVO> findByEmployeeEmail(String email);
    
    // For multiple results (if email can have multiple attachments)
    List<EmployeeAttachmentVO> findAllByEmployeeEmail(String email);
    
    // Explicit query for active records
    @Query("SELECT e FROM EmployeeAttachmentVO e WHERE e.active = :active")
    List<EmployeeAttachmentVO> findActiveRecords(@Param("active") String active);
    
    // Corrected query for pending scheduled emails
    @Query("SELECT e FROM EmployeeAttachmentVO e WHERE e.isScheduled = true AND e.isSent = false AND e.scheduledTime < :currentTime")
    List<EmployeeAttachmentVO> findPendingScheduledEmails(@Param("currentTime") LocalDateTime currentTime);
}