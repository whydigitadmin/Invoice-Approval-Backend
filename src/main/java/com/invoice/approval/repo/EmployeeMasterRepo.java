package com.invoice.approval.repo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.EmployeeMasterVO;

@Repository
public interface EmployeeMasterRepo extends JpaRepository<EmployeeMasterVO, Long> {

//	Object findById(Long employeeMasterId);
	
//	@Query(value = "select a from EmployeeMasterVO a where a.id=?1")
//	EmployeeMasterVO findByExpEmpId(Long id);
//	
//	 @Query("SELECT e FROM EmployeeMasterVO e WHERE e.active = 'T'")
//	    List<EmployeeMasterVO> findAll();

	@Query(nativeQuery = true,value = "select mg_employeemasterid,employee,code,department,designation,lvl,reportingto,reportingtocode,active,attachment,dob,branch,doj,mailid,mobile,subdepartment,vertical,costcenter,branchhead,regionalhead,verticalhead,corpteam from  mg_employeemaster ")
	Set<Object[]> getAllEmployees();

	boolean existsByCode(String code);
	
	 @Query("SELECT e.mailid FROM EmployeeMasterVO e WHERE e.code = ?1")
	    Optional<String> findEmailByCode(String employeeCode);

	 @Query("SELECT e FROM EmployeeMasterVO e WHERE MONTH(e.dob) = :month AND DAY(e.dob) = :day")
	    List<EmployeeMasterVO> findByDob(int month, int day);
	 
	 // Add this new method if you need to find by mobile
	    Optional<EmployeeMasterVO> findByMobile(String mobile);

}
