
package com.invoice.approval.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.PerformanceGoalsVO;


public interface PerformanceGoalsRepo extends JpaRepository<PerformanceGoalsVO, Long> {
	
	@Query(value = "select a from PerformanceGoalsVO a where a.id=?1")
	PerformanceGoalsVO findByPerformanceGoalsId(Long id);
	
	 @Query("SELECT e FROM PerformanceGoalsVO e ")
	    List<PerformanceGoalsVO> findAll();
	 
		@Query(nativeQuery = true,value = "select gst_performancegoalsid,appraisalyear,empcode,empname,approve1,approve1name,approve1on,pmonth From gst_performancegoals where empcode = ?1")
		Set<Object[]> getPerformanceGoalsbyUserName(String userName);
		
		@Query(nativeQuery = true,value = "select gst_performancegoalsid,appraisalyear,empcode,empname,reportingto,reportingname,pmonth From gst_performancegoals where reportingto = ?1")
		Set<Object[]> getPerformanceGoalsbyreportingto(String reportingto);
		
		@Query(nativeQuery = true,value = "select gst_performancegoalsdtlid,perspective,objectivedesc,perassigned,measurement,qtrtarget,performance,comments,selfrating,appraiserrating,performanceself,apprjustification From gst_performancegoalsdtl where gst_performancegoalsid = ?1")
		Set<Object[]> getPerformanceGoalsDtlbyid(Long id);

		@Query(nativeQuery =true,value = "select * from GST_PERFORMANCEGOALS where gst_performancegoalsid=?1")
		PerformanceGoalsVO findPerformancegoals(Long id);

		@Query(nativeQuery = true,value = "select gst_performancegoalsid,appraisalyear,empcode,empname,reportingto,reportingname,approve1,pmonth From gst_performancegoals where gst_performancegoalsid = ?1")
		Set<Object[]> getPerformanceGoalsVOListById(Long id);

}



