package com.invoice.approval.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.VendorDTO;
import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.entity.VendorVO;
import com.invoice.approval.repo.VendorRepo;

@Service
public class VendorServiceImpl implements VendorService{
	
	@Autowired
	VendorRepo vendorRepo;
	
	
	@Override
	public Map<String, Object> createVO(VendorDTO vendorDto) {
		String message;
		VendorVO vendorVO = null;

		// Check if ID is null for create or update operation
		if (ObjectUtils.isEmpty(vendorDto.getId())) {
			// Create operation
			vendorVO = new VendorVO();
			vendorVO.setCreatedBy(vendorDto.getCreatedBy());
			vendorVO.setModifiedBy(vendorDto.getCreatedBy());

			message = "PO created successfully";
		} else {
			// Update operation
			vendorVO = vendorRepo.findById(vendorDto.getId()).orElseThrow(
					() -> new EntityNotFoundException("Header Detail not found with ID: " + vendorDto.getId()));
			vendorVO.setModifiedBy(vendorDto.getCreatedBy());
			message = "Header Detail  updated successfully";
		}

		vendorVO = VendorVOFromVendorDTO(vendorVO, vendorDto);

		vendorRepo.save(vendorVO);

		// Prepare response
		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("vendorVO", vendorVO);
		return response;
	}

	private VendorVO VendorVOFromVendorDTO(VendorVO vendorVO, VendorDTO vendorDto) {
		// TODO Auto-generated method stub

		vendorVO.setVendorname(vendorDto.getVendorname());
		vendorVO.setAddress(vendorDto.getAddress());
		vendorVO.setGstin(vendorDto.getGstin());
		vendorVO.setPanno(vendorDto.getPanno());
		vendorVO.setContactno(vendorDto.getContactno());
		vendorVO.setContactperson(vendorDto.getContactperson());
		vendorVO.setEmailid(vendorDto.getEmailid());
		
		
		return vendorVO;

	}

	@Override
	public List<VendorVO> vendorVO() {
		
		return vendorRepo.findAll();
	}
	

	@Override
	public VendorVO getfindByVendor(String name) {
		// TODO Auto-generated method stub
		return vendorRepo.getfindByVendor(name);
	}

	
	


}
