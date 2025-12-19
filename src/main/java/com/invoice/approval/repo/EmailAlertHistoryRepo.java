package com.invoice.approval.repo;

import com.invoice.approval.entity.EmailAlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailAlertHistoryRepo extends JpaRepository<EmailAlertHistory, Long> {
    
    // **ALTERNATIVE: Use Interface Projection (Simpler)**
    @Query(value = "SELECT TRUNC(h.alert_date) as alertDate, " +
           "COUNT(*) as totalEmails, " +
           "SUM(CASE WHEN h.status = 'SUCCESS' THEN 1 ELSE 0 END) as successfulEmails, " +
           "SUM(CASE WHEN h.status = 'FAILED' THEN 1 ELSE 0 END) as failedEmails " +
           "FROM gst_emailhistory h " +
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
    
    @Query(value = "SELECT TRUNC(h.alert_date) as alert_date, " +
            "COUNT(*) as total_emails, " +
            "SUM(CASE WHEN h.status = 'SUCCESS' THEN 1 ELSE 0 END) as successful_emails, " +
            "SUM(CASE WHEN h.status = 'FAILED' THEN 1 ELSE 0 END) as failed_emails " +
            "FROM gst_emailhistory h " +
            "WHERE h.alert_date >= :startDate " +
            "GROUP BY TRUNC(h.alert_date) " +
            "ORDER BY TRUNC(h.alert_date) DESC", 
            nativeQuery = true)
     List<Object[]> getDailySummary(@Param("startDate") LocalDateTime startDate);
  
    // **Native query for recent email status - CORRECTED VERSION**
    @Query(value = "SELECT " +
            "h.mail_to as mailTo, " +
            "h.cc_emails as ccEmails, " +  // Now this column exists (added by Hibernate)
            "h.sales_person_name as salesPersonName, " +  // Now this column exists
            "h.customer_count as customerCount, " +  // Now this column exists
            "h.status, " +
            "h.error_message as errorMessage, " +
            "h.created_date as sentTime, " +
            "h.invoice_id as invoiceId, " +
            "h.vendor_id as vendorId, " +
            "h.vendor_name as vendorName " +
            "FROM gst_emailhistory h " +
            "WHERE TRUNC(h.created_date) = TRUNC(SYSDATE) " +  // For Oracle 11g
            "ORDER BY h.created_date DESC", 
            nativeQuery = true)
     List<Object[]> getTodayEmailStatus();
    
    // Additional helpful queries based on entity fields
    List<EmailAlertHistory> findByVendorId(Long vendorId);
    
    List<EmailAlertHistory> findByInvoiceId(Long invoiceId);
    
    List<EmailAlertHistory> findByVendorNameContainingIgnoreCase(String vendorName);
    
    List<EmailAlertHistory> findByStatusAndAlertDateBetween(String status, LocalDateTime startDate, LocalDateTime endDate);
    
    // Count queries
    Long countByStatus(String status);
    
    Long countByMailTo(String mailTo);
    
    // Find by created date range
    List<EmailAlertHistory> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find emails with error messages (failed emails)
    @Query("SELECT h FROM EmailAlertHistory h WHERE h.errorMessage IS NOT NULL AND h.errorMessage <> '' ORDER BY h.alertDate DESC")
    List<EmailAlertHistory> findEmailsWithErrors();
}