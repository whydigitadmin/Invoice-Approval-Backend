package com.invoice.approval.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.EmailSendingHistory;
import com.invoice.approval.entity.EmailStatus;

@Repository
public interface EmailSendingHistoryRepo extends JpaRepository<EmailSendingHistory, Long> {
    
    List<EmailSendingHistory> findBySentAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<EmailSendingHistory> findByStatus(EmailStatus status);
    
    List<EmailSendingHistory> findBySalespersonEmail(String salespersonEmail);
    
    List<EmailSendingHistory> findBySalespersonEmailAndSentAtBetween(
        String salespersonEmail, 
        LocalDateTime start, 
        LocalDateTime end
    );
    
    List<EmailSendingHistory> findByJobType(String jobType);
    
    List<EmailSendingHistory> findByStatusAndSentAtBetween(
        EmailStatus status, 
        LocalDateTime start, 
        LocalDateTime end
    );
    
    long countBySentAtBetween(LocalDateTime start, LocalDateTime end);
    
    long countByStatusAndSentAtBetween(EmailStatus status, LocalDateTime start, LocalDateTime end);
    
    long countByJobTypeAndSentAtBetween(String jobType, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT CAST(e.sentAt as date) as date, COUNT(e) as totalCount, " +
           "SUM(CASE WHEN e.status = 'SUCCESS' THEN 1 ELSE 0 END) as successCount " +
           "FROM EmailSendingHistory e WHERE e.sentAt >= :startDate " +
           "GROUP BY CAST(e.sentAt as date) ORDER BY CAST(e.sentAt as date)")
    List<Object[]> getDailySummary(@Param("startDate") LocalDateTime startDate);
    
    @Modifying
    @Query("DELETE FROM EmailSendingHistory e WHERE e.sentAt < :cutoffDate")
    int deleteBySentAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    List<EmailSendingHistory> findTop10ByOrderBySentAtDesc();
    
    List<EmailSendingHistory> findByErrorMessageIsNotNull();
    
    List<EmailSendingHistory> findByRecipientEmail(String recipientEmail);
}