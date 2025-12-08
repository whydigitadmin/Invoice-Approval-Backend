package com.invoice.approval.repo;

import com.invoice.approval.entity.EmailAlertHistory;
import com.invoice.approval.dto.EmailAlertStatsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailAlertHistoryRepo extends JpaRepository<EmailAlertHistory, Long> {
    
    // **USE THIS NATIVE QUERY - IT WORKS WITH ORACLE 11g**
    @Query(value = "SELECT TRUNC(h.alert_date) as alert_date, " +
           "COUNT(*) as total_emails, " +
           "SUM(CASE WHEN h.status = 'SUCCESS' THEN 1 ELSE 0 END) as successful_emails, " +
           "SUM(CASE WHEN h.status = 'FAILED' THEN 1 ELSE 0 END) as failed_emails " +
           "FROM gst_email_alert_history h " +
           "WHERE h.alert_date >= :startDate " +
           "GROUP BY TRUNC(h.alert_date) " +
           "ORDER BY TRUNC(h.alert_date) DESC", 
           nativeQuery = true)
    List<Object[]> getDailySummary(@Param("startDate") LocalDateTime startDate);
    
    // **ALTERNATIVE: Use Interface Projection (Simpler)**
    @Query(value = "SELECT TRUNC(h.alert_date) as alertDate, " +
           "COUNT(*) as totalEmails, " +
           "SUM(CASE WHEN h.status = 'SUCCESS' THEN 1 ELSE 0 END) as successfulEmails, " +
           "SUM(CASE WHEN h.status = 'FAILED' THEN 1 ELSE 0 END) as failedEmails " +
           "FROM gst_email_alert_history h " +
           "WHERE h.alert_date >= :startDate " +
           "GROUP BY TRUNC(h.alert_date) " +
           "ORDER BY TRUNC(h.alert_date) DESC", 
           nativeQuery = true)
    List<EmailAlertStatsProjection> getDailySummaryProjection(@Param("startDate") LocalDateTime startDate);
    
    // **SIMPLE JPQL WITHOUT DATE FUNCTION** (for basic queries)
    @Query("SELECT h FROM EmailAlertHistory h WHERE h.alertDate >= :startDate ORDER BY h.alertDate DESC")
    List<EmailAlertHistory> findByAlertDateAfter(@Param("startDate") LocalDateTime startDate);
    
    // Other query methods
    List<EmailAlertHistory> findByStatus(String status);
    
    
    List<EmailAlertHistory> findByAlertDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    Long countByStatusAndAlertDateBetween(String status, LocalDateTime startDate, LocalDateTime endDate);
    
    List<EmailAlertHistory> findByMailTo(String mailTo);
    
    // Interface Projection
    interface EmailAlertStatsProjection {
        java.util.Date getAlertDate();
        Long getTotalEmails();
        Long getSuccessfulEmails();
        Long getFailedEmails();
    }
}