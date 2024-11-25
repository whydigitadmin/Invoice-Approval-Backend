package com.invoice.approval.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.UserVO;

public interface UserRepo extends JpaRepository<UserVO, Long> {

	boolean existsByUserNameOrEmail(String userName, String email);

	@Query("select a from UserVO a where a.userName=?1")
	UserVO findByUserName(String userName);

	@Query(value = "select u from UserVO u where u.id =?1")
	UserVO getUserById(Long usersId);
	
	@Query(nativeQuery=true,value="SELECT a.username, a.branchcode from vg_userbranch a WHERE a.username=?1")
	Set<Object[]> getBranchCodeDetails(String userName);

	boolean existsByUserName(String userName);

}