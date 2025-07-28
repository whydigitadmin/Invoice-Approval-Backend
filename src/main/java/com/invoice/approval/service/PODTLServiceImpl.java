package com.invoice.approval.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.PODTO;
import com.invoice.approval.dto.PODTLDTO;
import com.invoice.approval.entity.PODTLVO;
import com.invoice.approval.entity.POVO;
import com.invoice.approval.repo.PODTLRepo;
import com.invoice.approval.repo.POVORepo;

@Service
public class PODTLServiceImpl implements PODTLService {

	@Autowired
	POVORepo povoRepo;

	@Autowired
	PODTLRepo detailRepo;

	@Override
	public Map<String, Object> createPO(PODTO poDto) {
		String message;
		POVO poVO = null;

		// Check if ID is null for create or update operation
		if (ObjectUtils.isEmpty(poDto.getId())) {
			// Create operation
			poVO = new POVO();
			poVO.setCreatedBy(poDto.getCreatedBy());
			poVO.setModifiedBy(poDto.getCreatedBy());

			message = "PO created successfully";
		} else {
			// Update operation
			poVO = povoRepo.findById(poDto.getId()).orElseThrow(
					() -> new EntityNotFoundException("Header Detail not found with ID: " + poDto.getId()));
			poVO.setModifiedBy(poDto.getCreatedBy());
			message = "Header Detail  updated successfully";
		}

		poVO = POVOFromPODTO(poVO, poDto);

		povoRepo.save(poVO);

		// Prepare response
		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("poVO", poVO);
		return response;
	}

	private POVO POVOFromPODTO(POVO poVO, PODTO poDto) {
		// TODO Auto-generated method stub

		poVO.setPoid(poDto.getPoid());
		poVO.setPodt(poDto.getPodt());
		poVO.setBranchname(poDto.getBranchname());
		poVO.setVendor(poDto.getVendor());
		poVO.setVendoraddress(poDto.getVendoraddress());
		poVO.setApprove(poDto.getApprove());
		poVO.setBranchname(poDto.getBranchname());
		poVO.setVendor(poDto.getVendor());
		poVO.setPartygstin(poDto.getPartygstin());	
		poVO.setQuoterefno(poDto.getQuoterefno());
		poVO.setRemarks(poDto.getRemarks());
		poVO.setApprovedby(poDto.getApprovedby());
		poVO.setApprovedon(poDto.getApprovedon());
		poVO.setEntity(poDto.getEntity());
		
		poVO.setBcurr(poDto.getBcurr());
		poVO.setExrate(poDto.getExrate());	
		poVO.setShippingaddress(poDto.getShippingaddress());
		poVO.setAddresstype(poDto.getAddresstype());
		poVO.setRemarks(poDto.getRemarks());
		poVO.setTerms(poDto.getTerms());
		poVO.setGstin(poDto.getGstin());
		poVO.setBillingstate(poDto.getBillingstate());
		poVO.setShippingplace(poDto.getShippingplace());

		if (ObjectUtils.isNotEmpty(poVO.getId())) {
			List<PODTLVO> podtlVO = detailRepo.findBypovo(poVO);
			detailRepo.deleteAll(podtlVO);
		}

		int tot = 0;

		List<PODTLVO> podtlDTOs = new ArrayList();
		for (PODTLDTO podtlDTO : poDto.getPodtlDto()) {
			PODTLVO podtlVO = new PODTLVO();
			podtlVO.setItem(podtlDTO.getItem());
			podtlVO.setDescription(podtlDTO.getDescription());
			podtlVO.setRate(podtlDTO.getRate());
			podtlVO.setQty(podtlDTO.getQty());
			float amt = podtlDTO.getRate() * podtlDTO.getQty();
			podtlVO.setLcamount(amt);

			tot += amt;

			podtlVO.setPovo(poVO);
			podtlDTOs.add(podtlVO);

		}

		poVO.setPodtl(podtlDTOs);
		poVO.setTotal(tot);
		return poVO;

	}

	@Override
	public List<POVO> poVO() {
		
		return povoRepo.findAll();
	}
	
	@Override
	public POVO podtlsVO(long id) {
		
		return povoRepo.findById(id).get();
	}

	
}
