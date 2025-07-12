package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.PerformanceGoalsDtlVO;
import com.invoice.approval.entity.PerformanceGoalsVO;

public interface PerformanceGoalsDtlRepo extends JpaRepository<PerformanceGoalsDtlVO, Long> {

    @Query("select a from PerformanceGoalsDtlVO a where a.id = ?1")
    PerformanceGoalsDtlVO getDetails(Long id);
    
    List<PerformanceGoalsDtlVO> findByPerformanceGoalsVO_Id(Long performanceGoalsId); // Optional: to get all child records by header ID
    
    void deleteByPerformanceGoalsVO(PerformanceGoalsVO performanceGoalsVO);
    
    List<PerformanceGoalsDtlVO> findByPerformanceGoalsVO(PerformanceGoalsVO performanceGoalsVO);
    
}
