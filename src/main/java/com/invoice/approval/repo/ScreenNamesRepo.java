package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.ScreenNamesVO;

public interface ScreenNamesRepo extends JpaRepository<ScreenNamesVO, Long> {

	
	@Query(nativeQuery = true, value = "select * from screenname where screennameid=?1")
	List<ScreenNamesVO> findFinScreenById(Long id);


	boolean existsByScreenName(String screenName);

	boolean existsByScreenCode(String screenCode);

	@Query("SELECT s FROM ScreenNamesVO s ORDER BY s.id DESC")
	List<ScreenNamesVO> findAllByOrderByIdDesc();



}
