package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.RolesVO;

public interface RolesRepo extends JpaRepository<RolesVO, Long> {


	@Query(value = "select a from RolesVO a where a.active=true")
	List<RolesVO> findAllActiveRoles();

	@Query(value = "select a from RolesVO a ")
	List<RolesVO> findAllRoles();

	boolean existsByRole(String role);

	RolesVO findByRole(String role);

}
