package com.invoice.approval.repo;

import com.invoice.approval.entity.SentinelRawJsonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SentinelJsonRepo extends JpaRepository<SentinelRawJsonData, Long> {
    
    Optional<SentinelRawJsonData> findByReferenceId(String referenceId);
    
    Optional<SentinelRawJsonData> findByJobNo(String jobNo);
    
    Optional<SentinelRawJsonData> findByJobNoAndDataType(String jobNo, String dataType);
    
    @Modifying
    @Transactional
    @Query("UPDATE SentinelRawJsonData s SET s.insertedFlag = 'T', s.jobId = :jobId, s.jobNo = :jobNo, s.referenceId = :referenceId, s.processedTimestamp = CURRENT_TIMESTAMP WHERE s.rawJsonId = :rawJsonId")
    void updateSuccessWithDetails(@Param("rawJsonId") Long rawJsonId, 
                                  @Param("jobId") String jobId, 
                                  @Param("jobNo") String jobNo, 
                                  @Param("referenceId") String referenceId);
    
    // New method to store jobNo in both columns
    @Modifying
    @Transactional
    @Query("UPDATE SentinelRawJsonData s SET s.insertedFlag = 'T', s.jobId = :jobNoValue, s.jobNo = :jobNoValue, s.referenceId = :referenceId, s.processedTimestamp = CURRENT_TIMESTAMP WHERE s.rawJsonId = :rawJsonId")
    void updateSuccessWithJobNo(@Param("rawJsonId") Long rawJsonId, 
                                @Param("jobNoValue") String jobNoValue, 
                                @Param("jobNoValue") String jobNo, 
                                @Param("referenceId") String referenceId);
    
    @Modifying
    @Transactional
    @Query("UPDATE SentinelRawJsonData s SET s.insertedFlag = 'F', s.errorMessage = :errorMessage, s.processedTimestamp = CURRENT_TIMESTAMP WHERE s.rawJsonId = :rawJsonId")
    void updateFailureWithDetails(@Param("rawJsonId") Long rawJsonId, @Param("errorMessage") String errorMessage);
}