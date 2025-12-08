
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
	 
		@Query(nativeQuery = true,value = "select gst_performancegoalsid,appraisalyear,empcode,empname,approve1,approve1name,approve1on,pmonth From gst_performancegoals where lower( empcode ) = lower( ?1 )")
		Set<Object[]> getPerformanceGoalsbyUserName(String userName);
		
		@Query(nativeQuery = true,value = "select gst_performancegoalsid,appraisalyear,empcode,empname,reportingto,reportingname,pmonth From gst_performancegoals where lower(reportingto) = lower( ?1 )")
		Set<Object[]> getPerformanceGoalsbyreportingto(String reportingto);
		
		@Query(nativeQuery = true,value = "select gst_performancegoalsdtlid,perspective,objectivedesc,perassigned,measurement,qtrtarget,performance,comments,selfrating,appraiserrating,performanceself,apprjustification From gst_performancegoalsdtl where gst_performancegoalsid = ?1")
		Set<Object[]> getPerformanceGoalsDtlbyid(Long id);

		@Query(nativeQuery =true,value = "select * from GST_PERFORMANCEGOALS where gst_performancegoalsid=?1")
		PerformanceGoalsVO findPerformancegoals(Long id);
		
		@Query(nativeQuery = true,value = "select reportingto,reportingtocode from MG_EMPLOYEEMASTER where lower( code ) = lower( ?1 )")
		Set<Object[]> getReportingUserName(String userName);
		
		@Query(nativeQuery = true,value = "select employee,code from mg_employeemaster where lower(code) = lower(?1)\r\n"
				+ "union\r\n"
				+ "select employee,code from mg_employeemaster where lower(reportingtocode) = lower(?1)\r\n"
				+ "union\r\n"
				+ "select employee,code from mg_employeemaster where lower(regionalhead) =( select  lower(employee) from mg_employeemaster where lower(code) = lower(?1))\r\n"
				+ "union\r\n"
				+ "select employee,code from mg_employeemaster where lower(verticalhead)  in ( select distinct lower(reportingto) from mg_employeemaster where lower(reportingtocode) =  lower(?1))\r\n"
				+ "union\r\n"
				+ "select employee,code from mg_employeemaster where active in (select active from mg_employeemaster where subdepartment = 'Human Resource Head' and lower(code) = lower(?1))"
				+ " order by employee")
		Set<Object[]> getDisplayEmpName(String userName);
		

		@Query(nativeQuery = true,value = "select gst_performancegoalsid,appraisalyear,empcode,empname,reportingto,reportingname,approve1,pmonth From gst_performancegoals where gst_performancegoalsid = ?1")
		Set<Object[]> getPerformanceGoalsVOListById(Long id);

}



