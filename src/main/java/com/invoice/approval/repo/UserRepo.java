package com.invoice.approval.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.UserVO;

public interface UserRepo extends JpaRepository<UserVO, Long> {


	@Query("select a from UserVO a where a.userName=?1")
	UserVO findByUserName(String userName);

	@Query(value = "select u from UserVO u where u.id =?1")
	UserVO getUserById(Long usersId);
	
	@Query(nativeQuery=true,value="SELECT distinct a.username, a.branchcode,a.branchname from vg_userbranch a WHERE a.username=?1\r\n"
			+ "union\r\n"
			+ "select username,'ALL','ALL' from axusers where allindia = 'T' and username =?1")
	Set<Object[]> getBranchCodeDetails(String userName);

	boolean existsByUserName(String userName);

	boolean existsByUserNameOrEmail(String userName,String email);

	UserVO findByUserNameOrEmail(String userName,String email);

}