package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.UserActionVO;



@Repository
public interface UserActionRepo extends JpaRepository<UserActionVO, Long>{

}
