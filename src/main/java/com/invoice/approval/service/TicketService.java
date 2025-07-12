package com.invoice.approval.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.CRPreAppDTO;
import com.invoice.approval.dto.TicketDTO;
import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.entity.TicketVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface TicketService {

	
	public Map<String, Object> updateCreateTicket(TicketDTO ticketDTO) throws ApplicationException;
	
	TicketVO updateApprove1(Long id, String approval, String createdby, String status,String solvedon);
	
	TicketVO updateNote(Long id, String createdby);
	
	TicketVO getfindByTicketId(Long id);
	
	void saveUploadFiles(List<MultipartFile> files, Long id) throws ApplicationException, IOException;

	List<Map<String, Object>> getTicketReport(String userName);
	
	List<Map<String, Object>> getAdminNote(String userName);
	
	List<Map<String, Object>> getUserNote(String userName);
	
	List<Map<String, Object>> getUserActiveStatus(String userName);
}




