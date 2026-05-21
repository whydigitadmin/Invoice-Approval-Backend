package com.invoice.approval.repo;

import com.invoice.approval.entity.QuoteRateVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface QuoteRateRepo extends JpaRepository<QuoteRateVO, Long> {
    
    List<QuoteRateVO> findByPolAndPod(String pol, String pod);
    
    List<QuoteRateVO> findByPol(String pol);
    
    List<QuoteRateVO> findByPod(String pod);
    
    List<QuoteRateVO> findByAirRateIsNotNull();
    
    List<QuoteRateVO> findBySeaRateIsNotNull();
    
    @Query("SELECT q FROM QuoteRateVO q WHERE " +
           "(:mode = 'AIR' AND q.airRate IS NOT NULL) OR " +
           "(:mode = 'SEA' AND q.seaRate IS NOT NULL) OR " +
           "(:mode = 'BOTH')")
    List<QuoteRateVO> findByMode(@Param("mode") String mode);
    
    @Query("SELECT DISTINCT q.pol FROM QuoteRateVO q WHERE q.pol IS NOT NULL")
    List<String> findDistinctPol();
    
    @Query("SELECT DISTINCT q.pod FROM QuoteRateVO q WHERE q.pod IS NOT NULL")
    List<String> findDistinctPod();
    
    @Query("SELECT q FROM QuoteRateVO q WHERE " +
           "LOWER(q.pol) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(q.pod) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<QuoteRateVO> searchByPolOrPod(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT q FROM QuoteRateVO q WHERE " +
           "(q.airRate BETWEEN :minPrice AND :maxPrice) OR " +
           "(q.seaRate BETWEEN :minPrice AND :maxPrice)")
    List<QuoteRateVO> findByPriceRange(@Param("minPrice") Double minPrice, 
                                      @Param("maxPrice") Double maxPrice);
    
    // NEW METHOD FOR EMAIL PROCESSING
    @Query("SELECT q FROM QuoteRateVO q WHERE " +
           "(:mode = 'AIR' AND q.airRate IS NOT NULL AND q.airRate > 0) OR " +
           "(:mode = 'SEA' AND q.seaRate IS NOT NULL AND q.seaRate > 0) AND " +
           "UPPER(q.pol) = UPPER(:pol) AND " +
           "UPPER(q.pod) = UPPER(:pod)")
    List<QuoteRateVO> findRatesForQuote(@Param("mode") String mode,
                                       @Param("pol") String pol,
                                       @Param("pod") String pod);
}