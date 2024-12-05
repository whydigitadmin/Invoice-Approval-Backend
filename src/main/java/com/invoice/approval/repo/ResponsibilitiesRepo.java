package com.invoice.approval.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.ResponsibilityVO;

public interface ResponsibilitiesRepo extends JpaRepository<ResponsibilityVO, Long> {


	@Query(value="select a.id,a.responsibility from ResponsibilityVO a where a.active=true")
	Set<Object[]> findActive();

	@Query(value="select a from ResponsibilityVO a  order by a.id desc")
	List<ResponsibilityVO> findAllResponsibility();

	@Query(value="select a from ResponsibilityVO a where a.active=true order by a.id desc")
	List<ResponsibilityVO> findAllActiveResponsibility();

	boolean existsByResponsibility(String responsibility);


}
