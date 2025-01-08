package com.invoice.approval.service;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.PartyDTO;
import com.invoice.approval.entity.PartyHdrVO;
import com.invoice.approval.entity.PartyMasterVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.PartyHdrRepo;
import com.invoice.approval.repo.PartyMasterRepo;

@Service
public class PartyServiceImpl implements PartyService {

	@Autowired
	PartyHdrRepo partyHdrRepo;
	

	@Autowired
	PartyMasterRepo partyMasterRepo;

	@Override
	public Map<String, Object> updateCreateParty(PartyDTO partyDTO) throws ApplicationException {

		String message = null;

		PartyHdrVO partyHdrVO;

		if (ObjectUtils.isEmpty(partyDTO.getId())) {

			partyHdrVO = new PartyHdrVO();

			partyHdrVO.setCreatedBy(partyDTO.getCreatedBy());
			partyHdrVO.setUpdatedBy(partyDTO.getCreatedBy());

			message = "Party Creation Succesfully";

		} else {

			partyHdrVO = partyHdrRepo.findById(partyDTO.getId())
					.orElseThrow(() -> new ApplicationException("party Not Found with id: " + partyDTO.getId()));
			partyHdrVO.setUpdatedBy(partyDTO.getCreatedBy());

			message = "Party Updation Successfully";
		}

		partyHdrVO = getPartyHdrVOFromPartyDTO(partyHdrVO, partyDTO);
		PartyHdrVO pVO= partyHdrRepo.save(partyHdrVO);

		PartyMasterVO partymasterVo =partyMasterRepo.findByPartyCode(partyDTO.getPartyCode()); 
		
			partymasterVo.setCreditLimit(partyDTO.getNcreditLimit());
			partymasterVo.setCreditDays(partyDTO.getNcreditDays());
			
			System.out.println("new credit: "+partymasterVo.getCreditDays());
			System.out.println("new credit Limit: "+partymasterVo.getCreditLimit());
			partyMasterRepo.save(partymasterVo);		

		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("partyHdrVO", partyHdrVO);
		return response;

	}

	private PartyHdrVO getPartyHdrVOFromPartyDTO(PartyHdrVO partyHdrVO, @Valid PartyDTO partyDTO) {

		partyHdrVO.setPartyCode(partyDTO.getPartyCode());
		partyHdrVO.setPartyName(partyDTO.getPartyName());

		partyHdrVO.setCategory(partyDTO.getCategory());
		partyHdrVO.setSalesPerson(partyDTO.getSalesPerson());

		partyHdrVO.setCreditLimit(partyDTO.getCreditLimit());
		partyHdrVO.setCreditDays(partyDTO.getCreditDays());
		partyHdrVO.setNcreditLimit(partyDTO.getNcreditLimit());
		partyHdrVO.setNcreditDays(partyDTO.getNcreditDays());

		return partyHdrVO;
	}

}
