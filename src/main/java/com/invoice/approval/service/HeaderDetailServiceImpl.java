package com.invoice.approval.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.HeaderDTO;
import com.invoice.approval.dto.HeaderDetailDTO;
import com.invoice.approval.entity.HeaderDetailVO;
import com.invoice.approval.entity.HeaderVO;
import com.invoice.approval.repo.HeaderDetailRepo;
import com.invoice.approval.repo.HeaderVORepo;

@Service
public class HeaderDetailServiceImpl implements HeaderDetailService {

	@Autowired
	HeaderVORepo headerVORepo;

	@Autowired
	HeaderDetailRepo detailRepo;

	@Override
	public Map<String, Object> createHeaderDetail(HeaderDTO headerDto) {
		String message;
		HeaderVO headerVO = null;

		// Check if ID is null for create or update operation
		if (ObjectUtils.isEmpty(headerDto.getId())) {
			// Create operation
			headerVO = new HeaderVO();
			headerVO.setCreatedBy(headerDto.getCreatedBy());
			headerVO.setModifiedBy(headerDto.getCreatedBy());

			message = "HeaderDetail created successfully";
		} else {
			// Update operation
			headerVO = headerVORepo.findById(headerDto.getId()).orElseThrow(
					() -> new EntityNotFoundException("Header Detail not found with ID: " + headerDto.getId()));
			headerVO.setModifiedBy(headerDto.getCreatedBy());
			message = "Header Detail  updated successfully";
		}

		headerVO = getHeaderVOFromHeaderDTO(headerVO, headerDto);

		headerVORepo.save(headerVO);

		// Prepare response
		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("headerVO", headerVO);
		return response;
	}

	private HeaderVO getHeaderVOFromHeaderDTO(HeaderVO headerVO, HeaderDTO headerDto) {
		// TODO Auto-generated method stub

		headerVO.setDocId(headerDto.getDocId());
		headerVO.setDocDt(headerDto.getDocDt());

		if (ObjectUtils.isNotEmpty(headerVO.getId())) {
			List<HeaderDetailVO> headerDetailVO = detailRepo.findByHeader(headerVO);
			detailRepo.deleteAll(headerDetailVO);
		}

		int tot = 0;

		List<HeaderDetailVO> headerDetailVOs = new ArrayList();
		for (HeaderDetailDTO headerDetailDTO : headerDto.getHeaderDetailDto()) {
			HeaderDetailVO headerDetailVO = new HeaderDetailVO();
			headerDetailVO.setCategory(headerDetailDTO.getCategory());
			headerDetailVO.setDescription(headerDetailDTO.getDescription());
			headerDetailVO.setRate(headerDetailDTO.getRate());
			headerDetailVO.setQty(headerDetailDTO.getQty());
			int amt = headerDetailDTO.getRate() * headerDetailDTO.getQty();
			headerDetailVO.setAmount(amt);

			tot += amt;

			headerDetailVO.setHeader(headerVO);
			headerDetailVOs.add(headerDetailVO);

		}

		headerVO.setHeaderDetail(headerDetailVOs);
		headerVO.setTotal(tot);
		return headerVO;

	}

	@Override
	public List<HeaderVO> headerVO() {
		
		return headerVORepo.findAll();
	}
	
	@Override
	public HeaderVO headerDetailsVO(long id) {
		
		return headerVORepo.findById(id).get();
	}

}
