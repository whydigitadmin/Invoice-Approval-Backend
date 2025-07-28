package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import com.invoice.approval.dto.VendorDTO;
import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.entity.VendorVO;



public interface VendorService {
	


	List<VendorVO> vendorVO();

	Map<String, Object> createVO(VendorDTO vendorDto);

	
	VendorVO getfindByVendor(String name);
	

}
