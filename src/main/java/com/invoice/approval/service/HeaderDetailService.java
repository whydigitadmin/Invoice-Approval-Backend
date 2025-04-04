package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.invoice.approval.dto.HeaderDTO;
import com.invoice.approval.entity.HeaderVO;

@Service
public interface HeaderDetailService {

	Map<String, Object> createHeaderDetail(HeaderDTO headerDto);

	List<HeaderVO> headerVO();

	HeaderVO headerDetailsVO(long id);
}
