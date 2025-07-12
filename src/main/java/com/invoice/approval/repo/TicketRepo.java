package com.invoice.approval.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.invoice.approval.entity.TicketVO;

public interface TicketRepo extends JpaRepository<TicketVO, Long> {
	

	
//	@Query(value = "select a from TicketVO a where a.id=?1")
//	TicketVO findById(Long id);

//	
//	@Query(value = "select a from TicketVO a where a.id=?1")
//	TicketVO findByTicketId(Long id);
	
	
	
			@Query(nativeQuery = true,value = "select is_active from  users where username  = ?1")
	Set<Object[]> getUserActiveStatus(String userName);
	
	@Query(nativeQuery = true,value = "select gst_ticketId,title,description,status,assignto,solvedon,solvedby,createdOn from gst_ticket where (createdby = ?1 or 'admin'= ?1 )  order by createdon desc")
	Set<Object[]> getTicketReport(String userName);
	
	@Query(nativeQuery = true,value = "select gst_ticketId,title,description,status,assignto,solvedon,solvedby,a.createdOn,adminNote,userNote,employeename createdBy from gst_ticket a,users b\r\n"
			+ " where usernote = 'F' and adminnote='F' and 'admin'= ?1  and a.createdby = b.username order by createdon desc")
	Set<Object[]> getAdminNote(String userName);
	
	@Query(nativeQuery = true,value = "select gst_ticketId,title,description,status,assignto,solvedon,solvedby,createdOn,adminNote,userNote from gst_ticket where status = 'Completed' and usernote = 'F' and createdby = ?1 order by createdon desc")
	Set<Object[]> getUserNote(String userName);
}
