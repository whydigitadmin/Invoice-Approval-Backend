package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.PreGoalsDtlVO;
import com.invoice.approval.entity.PreGoalsVO;

public interface PreGoalsDtlRepo extends JpaRepository<PreGoalsDtlVO, Long> {

    @Query("select a from PreGoalsDtlVO a where a.id = ?1")
    PreGoalsDtlVO getDetails(Long id);
    
    List<PreGoalsDtlVO> findByPreGoalsVO_Id(Long preGoalsId); // Optional: to get all child records by header ID
    
    void deleteByPreGoalsVO(PreGoalsVO preGoalsVO);
    
    List<PreGoalsDtlVO> findByPreGoalsVO(PreGoalsVO preGoalsVO);
    
}
