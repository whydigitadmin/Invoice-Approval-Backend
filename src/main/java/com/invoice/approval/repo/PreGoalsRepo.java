package com.invoice.approval.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.PreGoalsVO;


public interface PreGoalsRepo extends JpaRepository<PreGoalsVO, Long> {
	
	@Query(value = "select a from PreGoalsVO a where a.id=?1")
	PreGoalsVO findByPreGoalsId(Long id);
	
	 @Query("SELECT e FROM PreGoalsVO e ")
	    List<PreGoalsVO> findAll();
	 
		@Query(nativeQuery = true,value = "select gst_pregoalsid,appraisalyear,empcode,empname,approve1,approve1name,approve1on From gst_pregoals where empcode = ?1")
		Set<Object[]> getPreGoalsbyUserName(String userName);
		
		@Query(nativeQuery = true,value = "select gst_pregoalsid,appraisalyear,empcode,empname,reportingto,reportingname From gst_pregoals where reportingto = ?1")
		Set<Object[]> getPreGoalsbyreportingto(String reportingto);
		
		@Query(nativeQuery = true,value = "select gst_pregoalsid,area,goals,selfinput,rating,score From gst_pregoalsdtl where gst_pregoalsid = ?1")
		Set<Object[]> getPreGoalsDtlbyid(Long id);

		@Query(nativeQuery =true,value = "select * from GST_PREGOALS where gst_pregoalsid=?1")
		PreGoalsVO findPregoals(Long id);

		@Query(nativeQuery = true,value = "select gst_pregoalsid,appraisalyear,empcode,empname,reportingto,reportingname,approve1 From gst_pregoals where gst_pregoalsid = ?1")
		Set<Object[]> getPreGoalsVOListById(Long id);

}



