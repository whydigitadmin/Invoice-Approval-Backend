package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.HeaderVO;

@Repository
public interface HeaderVORepo extends JpaRepository<HeaderVO, Long> {

}
