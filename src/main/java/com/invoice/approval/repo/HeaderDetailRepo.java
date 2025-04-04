package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.HeaderDetailVO;
import com.invoice.approval.entity.HeaderVO;

@Repository
public interface HeaderDetailRepo extends JpaRepository<HeaderDetailVO, Long> {

	List<HeaderDetailVO> findByHeader(HeaderVO headerVO);

}
