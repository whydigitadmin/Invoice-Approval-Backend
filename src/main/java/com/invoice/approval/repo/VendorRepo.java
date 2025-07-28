package com.invoice.approval.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.entity.VendorVO;

public interface VendorRepo extends JpaRepository<VendorVO, Long>{
	
	

	@Query(value = "select a from VendorVO a where a.vendorname=?1")
	VendorVO getfindByVendor(String name);
	
	
	
}
