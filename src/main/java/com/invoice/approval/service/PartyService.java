package com.invoice.approval.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.invoice.approval.dto.PartyDTO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface PartyService {

	

	public Map<String, Object> updateCreateParty(PartyDTO partyDTO) throws ApplicationException;
	
	
}
