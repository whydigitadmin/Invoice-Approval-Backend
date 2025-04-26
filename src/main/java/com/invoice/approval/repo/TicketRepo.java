package com.invoice.approval.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.invoice.approval.entity.TicketVO;

public interface TicketRepo extends JpaRepository<TicketVO, Long> {
	

	
//	@Query(value = "select a from TicketVO a where a.id=?1")
//	TicketVO findById(Long id);

	
	
	@Query(nativeQuery = true,value = "select gst_ticketId,title,description,status,assignto,solvedon,solvedby,createdOn from gst_ticket where (createdby = ?1 or 'admin'= ?1 )  order by createdon desc")
	Set<Object[]> getTicketReport(String userName);
}
