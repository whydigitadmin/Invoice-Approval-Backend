package com.invoice.approval.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.invoice.approval.entity.PODTLVO;
import com.invoice.approval.entity.POVO;

@Repository
public interface PODTLRepo extends JpaRepository<PODTLVO, Long>{

	
	
	List<PODTLVO> findBypovo(POVO povo);
}



