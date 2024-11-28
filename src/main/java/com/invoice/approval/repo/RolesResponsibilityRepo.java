package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.RolesResponsibilityVO;
import com.invoice.approval.entity.RolesVO;

public interface RolesResponsibilityRepo extends JpaRepository<RolesResponsibilityVO, Long>{

	List<RolesResponsibilityVO> findByRolesVO(RolesVO rolesVO);

}
