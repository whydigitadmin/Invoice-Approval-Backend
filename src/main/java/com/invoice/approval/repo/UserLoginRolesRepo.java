package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.UserLoginRolesVO;
import com.invoice.approval.entity.UserVO;

public interface UserLoginRolesRepo extends JpaRepository<UserLoginRolesVO, Long> {
	
	List<UserLoginRolesVO> findByUserVO(UserVO userVO);

}
