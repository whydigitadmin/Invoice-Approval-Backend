package com.invoice.approval.repo;

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

	@Query(nativeQuery = true,value = "select mg_employeemasterid,employee,code,department,designation,lvl,reportingto,reportingtocode,active,attachment,dob,branch,doj from  mg_employeemaster ")
	Set<Object[]> getAllEmployees();

	boolean existsByCode(String code);

	

}
