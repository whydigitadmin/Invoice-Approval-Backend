package com.invoice.approval.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.invoice.approval.dto.DocTypeDTO;
import com.invoice.approval.dto.DocTypeMappingDTO;
import com.invoice.approval.entity.DocTypeMappingVO;
import com.invoice.approval.entity.DocTypeVO;
import com.invoice.approval.exception.ApplicationException;

@Service
public interface MasterService {

	DocTypeVO createDocType(DocTypeDTO docTypeDTO) throws ApplicationException;

	List<Map<String, Object>> getPendingDocTypeMapping(String branch, String branchCode, int finYear, int finYearId)
			throws NumberFormatException, ApplicationException;

	DocTypeMappingVO createDocTypeMappingVO(DocTypeMappingDTO docTypeMappingDTO) throws ApplicationException;

	String getDocid(String branch, int finYear, String screenCode) throws ApplicationException;

}
