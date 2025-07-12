package com.invoice.approval.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.TicketDTO;
import com.invoice.approval.entity.TicketAttachmentVO;
import com.invoice.approval.entity.TicketVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.TicketRepo;

@Service
public class TicketServiceImpl implements TicketService {

	@Autowired
	TicketRepo ticketRepo;

	@Override
	public Map<String, Object> updateCreateTicket(TicketDTO ticketDTO) throws ApplicationException {

		String message = null;

		TicketVO ticketVO = new TicketVO();

		if (ObjectUtils.isEmpty(ticketDTO.getId())) {

			ticketVO = new TicketVO();

			ticketVO.setCreatedBy(ticketDTO.getCreatedBy());
			ticketVO.setUpdatedBy(ticketDTO.getCreatedBy());

			message = "Ticket Creation Succesfully";

		}

		ticketVO = getTicketVOFromTicketDTO(ticketVO, ticketDTO);
		ticketRepo.save(ticketVO);

		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("ticketVO", ticketVO);
		return response;

	}

	private TicketVO getTicketVOFromTicketDTO(TicketVO ticketVO, TicketDTO ticketDTO) {

		ticketVO.setTitle(ticketDTO.getTitle());
		ticketVO.setDescription(ticketDTO.getDescription());
		return ticketVO;
	}

	@Override
	public TicketVO updateApprove1(Long id, String approval, String createdby, String status,String solvedon) {
		TicketVO ticketVO = ticketRepo.findById(id).get();
		if (ticketVO != null) {
			
				if (approval.equals("1")) {
					
					LocalDateTime now = LocalDateTime.now();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
					ticketVO.setSolvedBy(createdby);
					ticketVO.setSolvedOn(solvedon);
					ticketVO.setStatus(status);

				

			}
		}

		return ticketRepo.save(ticketVO);
	}

	
	
	@Override
	public TicketVO updateNote(Long id, String createdby) {
		TicketVO ticketVO = ticketRepo.findById(id).get();
		if (ticketVO != null) {
			
				if (createdby.equals("admin")) {
					
					
					ticketVO.setAdminNote("T");
					
			}
				else
				{
					ticketVO.setUserNote("T");
				}
				
		}

		return ticketRepo.save(ticketVO);
	}
	
	private List<Map<String, Object>> pendingDetails(Set<Object[]> details) {
		List<Map<String, Object>> report = new ArrayList<>();
		for (Object[] det : details) {
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl = new HashMap<>();
			dtl.put("gst_ticketId", det[0]);
			dtl.put("title", det[1] != null ? det[1].toString() : "");
			dtl.put("description", det[2] != null ? det[2].toString() : "");
			dtl.put("status", det[3] != null ? det[3].toString() : "");
			dtl.put("assignTo", det[4] != null ? det[4].toString() : "");
			dtl.put("solvedOn", det[5] != null ? det[5].toString().split(" ")[0] : "");
			dtl.put("sovledBy", det[6] != null ? det[6].toString() : "");
			dtl.put("createdOn", det[7] != null ? det[7].toString().split(" ")[0] : "");
			report.add(dtl);
		}
		return report;
	}
	

	@Override
	public List<Map<String, Object>> getTicketReport(String userName) {
		Set<Object[]> details = new HashSet<>();

		details = ticketRepo.getTicketReport(userName);

		return pendingDetails(details);
	}

	
	
	private List<Map<String, Object>> getAdminNote(Set<Object[]> details) {
		List<Map<String, Object>> report = new ArrayList<>();
		for (Object[] det : details) {
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl = new HashMap<>();
			dtl.put("gst_ticketId", det[0]);
			dtl.put("title", det[1] != null ? det[1].toString() : "");
			dtl.put("description", det[2] != null ? det[2].toString() : "");
			dtl.put("status", det[3] != null ? det[3].toString() : "");
			dtl.put("assignTo", det[4] != null ? det[4].toString() : "");
			dtl.put("solvedOn", det[5] != null ? det[5].toString().split(" ")[0] : "");
			dtl.put("sovledBy", det[6] != null ? det[6].toString() : "");
			dtl.put("createdOn", det[7] != null ? det[7].toString().split(" ")[0] : "");
			dtl.put("adminNote", det[8] != null ? det[8].toString() : "");
			dtl.put("userNote", det[9] != null ? det[9].toString() : "");
			dtl.put("createdBy", det[10] != null ? det[10].toString() : "");
			report.add(dtl);
		}
		return report;
	}
	

	@Override
	public List<Map<String, Object>> getAdminNote(String userName) {
		Set<Object[]> details = new HashSet<>();

		details = ticketRepo.getAdminNote(userName);

		return getAdminNote(details);
	}
	
	
	
	private List<Map<String, Object>> getUserActiveStatus(Set<Object[]> details) {
		List<Map<String, Object>> report = new ArrayList<>();
		for (Object[] det : details) {
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl = new HashMap<>();
			dtl.put("is_active", det[0]);	
			report.add(dtl);
		}
		return report;
	}
	

	@Override
	public List<Map<String, Object>> getUserActiveStatus(String userName) {
		Set<Object[]> details = new HashSet<>();

		details = ticketRepo.getUserActiveStatus(userName);

		return getUserActiveStatus(details);
	}

	
	
	private List<Map<String, Object>> getUserNote(Set<Object[]> details) {
		List<Map<String, Object>> report = new ArrayList<>();
		for (Object[] det : details) {
			DecimalFormat df = new DecimalFormat("0.00");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> dtl = new HashMap<>();
			dtl.put("gst_ticketId", det[0]);
			dtl.put("title", det[1] != null ? det[1].toString() : "");
			dtl.put("description", det[2] != null ? det[2].toString() : "");
			dtl.put("status", det[3] != null ? det[3].toString() : "");
			dtl.put("assignTo", det[4] != null ? det[4].toString() : "");
			dtl.put("solvedOn", det[5] != null ? det[5].toString().split(" ")[0] : "");
			dtl.put("sovledBy", det[6] != null ? det[6].toString() : "");
			dtl.put("createdOn", det[7] != null ? det[7].toString().split(" ")[0] : "");
			dtl.put("adminNote", det[8] != null ? det[8].toString() : "");
			dtl.put("userNote", det[9] != null ? det[9].toString() : "");
			report.add(dtl);
		}
		return report;
	}
	

	@Override
	public List<Map<String, Object>> getUserNote(String userName) {
		Set<Object[]> details = new HashSet<>();

		details = ticketRepo.getUserNote(userName);

		return getUserNote(details);
	}
	
	@Override
	public TicketVO getfindByTicketId(Long id) {
		return ticketRepo.findById(id).get();
	}

	@Override
	public void saveUploadFiles(List<MultipartFile> files, Long id) throws ApplicationException, IOException {
		final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

		// Fetch ticket
		TicketVO ticketVO = ticketRepo.findById(id).get();

		List<TicketAttachmentVO> ticketAttachmentVOList = new ArrayList<>();

		for (MultipartFile file : files) {
			if (file.getSize() > MAX_FILE_SIZE) {
				throw new ApplicationException("File size exceeds maximum limit of 5MB: " + file.getOriginalFilename());
			}

			TicketAttachmentVO attachment = new TicketAttachmentVO();
			attachment.setAttachment(file.getBytes());
			attachment.setTicketVO(ticketVO);

			ticketAttachmentVOList.add(attachment);
		}

		// Attach files to the ticket and save
		ticketVO.setTicketAttachmentVO(ticketAttachmentVOList);
		ticketRepo.save(ticketVO);
	}

}
