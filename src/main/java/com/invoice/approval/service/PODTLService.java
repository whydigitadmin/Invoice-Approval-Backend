package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.invoice.approval.dto.PODTO;
import com.invoice.approval.entity.POVO;

@Service

public interface PODTLService {

	Map<String, Object> createPO(PODTO poDto);

	List<POVO> poVO();

	POVO podtlsVO(long id);

	

	

}




