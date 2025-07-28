package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.POVO;

@Repository
public interface POVORepo extends JpaRepository<POVO, Long>{

}


