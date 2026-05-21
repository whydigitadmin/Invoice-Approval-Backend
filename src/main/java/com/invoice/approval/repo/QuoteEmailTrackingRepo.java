package com.invoice.approval.repo;

import com.invoice.approval.entity.QuoteEmailTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuoteEmailTrackingRepo extends JpaRepository<QuoteEmailTracking, Long> {
    
    List<QuoteEmailTracking> findByEmailHash(String emailHash);
    
    List<QuoteEmailTracking> findByIsReplied(String isReplied);
    
    @Query("SELECT e FROM QuoteEmailTracking e WHERE e.replyStatus LIKE %:status%")
    List<QuoteEmailTracking> findByReplyStatusContaining(@Param("status") String status);
    
    @Query("SELECT e FROM QuoteEmailTracking e ORDER BY e.lastRepliedDate DESC NULLS LAST, e.firstReceivedDate DESC")
    List<QuoteEmailTracking> findAllOrderByDateDesc();
    
    // FIX: Use native query for Oracle
    @Query(value = "SELECT COUNT(*) FROM quote_email_tracking WHERE is_replied = 'T' AND TRUNC(last_replied_date) = TRUNC(SYSDATE)", 
           nativeQuery = true)
    Long countRepliedToday();
    
    @Query("SELECT e FROM QuoteEmailTracking e WHERE e.firstReceivedDate < :cutoffDate")
    List<QuoteEmailTracking> findOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Modifying
    @Query("DELETE FROM QuoteEmailTracking e WHERE e.firstReceivedDate < :cutoffDate")
    int deleteByCreatedDateBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // NEW METHODS FOR EMAIL PROCESSING
    @Query("SELECT e FROM QuoteEmailTracking e WHERE e.isReplied = 'F' AND e.replyStatus NOT LIKE '%TEST%'")
    List<QuoteEmailTracking> findPendingQuotes();
    
    @Query("SELECT e FROM QuoteEmailTracking e WHERE LOWER(e.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<QuoteEmailTracking> findBySubjectKeyword(@Param("keyword") String keyword);
    
    List<QuoteEmailTracking> findByFromEmail(String fromEmail);
    
    @Query("SELECT e FROM QuoteEmailTracking e WHERE e.firstReceivedDate >= :startDate AND e.firstReceivedDate <= :endDate")
    List<QuoteEmailTracking> findBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
}