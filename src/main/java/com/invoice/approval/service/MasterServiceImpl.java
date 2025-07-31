package com.invoice.approval.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.DocTypeDTO;
import com.invoice.approval.dto.DocTypeMappingDTO;
import com.invoice.approval.dto.DocTypeMappingDetailsDTO;
import com.invoice.approval.entity.BranchVO;
import com.invoice.approval.entity.DocTypeMappingDetailsVO;
import com.invoice.approval.entity.DocTypeMappingVO;
import com.invoice.approval.entity.DocTypeVO;
import com.invoice.approval.entity.FinancialYearVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.BranchRepo;
import com.invoice.approval.repo.DocTypeMappingDetailsRepo;
import com.invoice.approval.repo.DocTypeMappingRepo;
import com.invoice.approval.repo.DocTypeRepo;
import com.invoice.approval.repo.FinancialYearRepo;

@Service
public class MasterServiceImpl implements MasterService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MasterServiceImpl.class);
	
	@Autowired
	DocTypeRepo docTypeRepo;
	
	@Autowired
	BranchRepo branchRepo;

	@Autowired
	DocTypeMappingRepo docTypeMappingRepo;
	
	@Autowired
	DocTypeMappingDetailsRepo docTypeMappingDetailsRepo;
	
	@Autowired
	FinancialYearRepo financialYearRepo;


	@Override
	public DocTypeVO createDocType(DocTypeDTO docTypeDTO) throws ApplicationException {

		DocTypeVO docTypeVO = new DocTypeVO();

		if (docTypeRepo.existsByScreenCode(docTypeDTO.getScreenCode())) {
			String errorMessage = String.format("The Screen Code: %s already exists ", docTypeDTO.getScreenCode());
			throw new ApplicationException(errorMessage);
		}

		if (docTypeRepo.existsByDocCode(docTypeDTO.getDocCode())) {
			String errorMessage = String.format("The Doc Code: %s already exists ", docTypeDTO.getScreenCode());
			throw new ApplicationException(errorMessage);
		}
		if (docTypeRepo.existsByScreenName(docTypeDTO.getScreenName())) {
			String errorMessage = String.format("The Screen Name: %s already exists ", docTypeDTO.getScreenName());
			throw new ApplicationException(errorMessage);
		}

		docTypeVO.setOrgId(docTypeDTO.getOrgId());
		docTypeVO.setScreenCode(docTypeDTO.getScreenCode());
		docTypeVO.setScreenName(docTypeDTO.getScreenName());
		docTypeVO.setDocCode(docTypeDTO.getDocCode());
		docTypeVO.setDocCodePos(docTypeDTO.getDocCodePos());
		docTypeVO.setBranchCodePos(docTypeDTO.getBranchCodePos());
		docTypeVO.setFinYearPos(docTypeDTO.getFinYearPos());
		docTypeVO.setSeqPos(docTypeDTO.getSeqPos());
		docTypeVO.setSeqDigit(docTypeDTO.getSeqDigit());
		docTypeVO.setCodePattern(docTypeDTO.getCodePattern());

		docTypeRepo.save(docTypeVO);

		return docTypeVO;
	}

	@Override
	public List<Map<String, Object>> getPendingDocTypeMapping(String branch, String branchCode, int finYear,
			int finYearId) throws NumberFormatException, ApplicationException {

		Set<Object[]> pendingDocTypeDetails = docTypeMappingRepo.getPendingDocTypeMappingDetails(branch, branchCode,
				finYear, finYearId);
		return getDetails(pendingDocTypeDetails);
	}

	private List<Map<String, Object>> getDetails(Set<Object[]> pendingDocTypeDetails)
			throws NumberFormatException, ApplicationException {

		List<Map<String, Object>> detailsList = new ArrayList<>();

		for (Object[] record : pendingDocTypeDetails) {
			Map<String, Object> map = new HashMap<>();
			map.put("branch", record[0] != null ? record[0].toString() : null);
			map.put("branchCode", record[1] != null ? record[1].toString() : null);
			map.put("finYear", record[2] != null ? record[2].toString() : null);
			map.put("finYearId", record[3] != null ? record[3].toString() : null);
			map.put("docCode", record[4] != null ? record[4].toString() : null);
			map.put("screenCode", record[5] != null ? record[5].toString() : null);
			map.put("screenName", record[6] != null ? record[6].toString() : null);

			String prefix = generateDocIdUsingPrefix(record[0].toString(), Integer.parseInt(record[2].toString()),
					record[5].toString());
			map.put("prefix", prefix != null ? prefix : null);
			map.put("lastNo", 1);

			detailsList.add(map);
		}
		return detailsList;

	}

	public String generateDocIdUsingPrefix(String branch, int finYear, String screenCode) throws ApplicationException {

		DocTypeVO config = docTypeRepo.findByScreenCode(screenCode)
				.orElseThrow(() -> new RuntimeException("No code config found for screenCode: " + screenCode));

		FinancialYearVO financialYearVO = financialYearRepo.findByFinYear(finYear);
		BranchVO branchVO = branchRepo.findByBranch(branch);
		// Step 3: Prepare value map
		Map<String, Object> values = new HashMap<>();
		if (config.getDocCode() != null) {
			values.put("docCode", config.getDocCode());
		}
		if (branchVO.getBranchCode() != null) {
			values.put("branchCode", branchVO.getBranchCode());
		} else {
			throw new ApplicationException("Branch Does not have Branch Code or not Found: " + branch);
		}
		if (financialYearVO.getFinYrIdentifier() != null) {
			values.put("finYear", financialYearVO.getFinYrIdentifier());
		} else {
			throw new ApplicationException("FinYear Does not have Finyear ID or not Found: " + finYear);
		}

		// Step 4: Replace pattern dynamically
		String code = resolvePatternWithSmartSkippingNew(config.getCodePattern(), values);
		System.out.println("EmployeeCode: " + code);
		return code;
	}

	private String resolvePatternWithSmartSkippingNew(String pattern, Map<String, Object> values) {
		Pattern regex = Pattern.compile("\\$\\{(.*?)}");
		Matcher matcher = regex.matcher(pattern);

		StringBuilder result = new StringBuilder();
		int lastIndex = 0;
		while (matcher.find()) {
			String placeholder = matcher.group(1); // e.g., companyCode
			Object value = values.get(placeholder);

			// Extract separator text before placeholder
			String separator = pattern.substring(lastIndex, matcher.start());

			// Include only if value is not zero
			if (value != null && !(value instanceof Integer && (Integer) value == 0)) {
				result.append(separator).append(value);
			}

			lastIndex = matcher.end();
		}

		// Append trailing part after last placeholder
		result.append(pattern.substring(lastIndex));

		// Optional cleanup
		return result.toString().replaceAll("[-_/\\.]{2,}", "-") // prevent multiple symbols
				.replaceAll("^[-_/\\.]+|[-_/\\.]+$", ""); // trim ends
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public DocTypeMappingVO createDocTypeMappingVO(DocTypeMappingDTO docTypeMappingDTO) throws ApplicationException {

		DocTypeMappingVO docTypeMappingVO = new DocTypeMappingVO();
		docTypeMappingVO.setOrgId(docTypeMappingDTO.getOrgId());
		docTypeMappingVO.setBranch(docTypeMappingDTO.getBranch());
		docTypeMappingVO.setBranchCode(docTypeMappingDTO.getBranchCode());
		docTypeMappingVO.setCreatedBy(docTypeMappingDTO.getCreatedBy());
		docTypeMappingVO.setFinYear(docTypeMappingDTO.getFinYear());
		docTypeMappingVO.setFinYearId(docTypeMappingDTO.getFinYearId());
		docTypeMappingVO.setModifiedBy(docTypeMappingDTO.getCreatedBy());

		List<DocTypeMappingDetailsVO> detailsVOs = new ArrayList<>();

		if (docTypeMappingDTO.getDocTypeMappingDetailsDTO() != null) {
			for (DocTypeMappingDetailsDTO detailsDTO : docTypeMappingDTO.getDocTypeMappingDetailsDTO())
			{
				DocTypeMappingDetailsVO documentTypeMappingDetailsVO = new DocTypeMappingDetailsVO();
				documentTypeMappingDetailsVO.setBranch(detailsDTO.getBranch());
		        documentTypeMappingDetailsVO.setBranchCode(detailsDTO.getBranchCode());
		        documentTypeMappingDetailsVO.setFinYear(detailsDTO.getFinYear());
		        documentTypeMappingDetailsVO.setFinYearId(detailsDTO.getFinYearId());
		        documentTypeMappingDetailsVO.setScreenCode(detailsDTO.getScreenCode());
		        documentTypeMappingDetailsVO.setScreenName(detailsDTO.getScreenName());
		        documentTypeMappingDetailsVO.setDocCode(detailsDTO.getDocCode());
		        documentTypeMappingDetailsVO.setPrefix(detailsDTO.getPrefix());
		        documentTypeMappingDetailsVO.setLastNo(detailsDTO.getLastNo());
		        documentTypeMappingDetailsVO.setOrgId(docTypeMappingDTO.getOrgId());
		        documentTypeMappingDetailsVO.setDocTypeMappingVO(docTypeMappingVO);
		        detailsVOs.add(documentTypeMappingDetailsVO);
			}
		}
		
		docTypeMappingVO.setDocumentTypeMappingDetailsVO(detailsVOs);
		docTypeMappingRepo.save(docTypeMappingVO);
		return docTypeMappingVO;
	}
	
	@Override
	@Transactional
	public String getDocid(String branch,int finYear,String screenCode) throws ApplicationException {
		
		return generateDocId(branch,finYear,screenCode);
		
	}
	
	public String generateDocId(String branch, int finYear, String screenCode) throws ApplicationException {

		DocTypeVO config = docTypeRepo.findByScreenCode(screenCode)
				.orElseThrow(() -> new RuntimeException("No code config found for screenCode: " + screenCode));

		DocTypeMappingDetailsVO docTypeMappingDetailsVO= docTypeMappingDetailsRepo.findByBranchAndFinYearAndScreenCode(branch,finYear,screenCode);
		FinancialYearVO financialYearVO = financialYearRepo.findByFinYear(finYear);
		BranchVO branchVO = branchRepo.findByBranch(branch);
		// Step 3: Prepare value map
		Map<String, Object> values = new HashMap<>();
		if (config.getDocCode() != null) {
			values.put("docCode", config.getDocCode());
		}
		if (branchVO.getBranchCode() != null) {
			values.put("branchCode", branchVO.getBranchCode());
		} else {
			throw new ApplicationException("Branch Does not have Branch Code or not Found: " + branch);
		}
		if (financialYearVO.getFinYrIdentifier() != null) {
			values.put("finYear", financialYearVO.getFinYrIdentifier());
		} else {
			throw new ApplicationException("FinYear Does not have Finyear ID or not Found: " + finYear);
		}
		
		String paddedSeq = String.format("%0" + config.getSeqDigit() + "d",docTypeMappingDetailsVO.getLastNo());
		values.put("seq", paddedSeq);
		
		

		// Step 4: Replace pattern dynamically
		String code = resolvePatternWithSmartSkipping(config.getCodePattern(), values);
		System.out.println("EmployeeCode: " + code);
		return code;
	}
	
	private String resolvePatternWithSmartSkipping(String pattern, Map<String, Object> values) {
		Pattern regex = Pattern.compile("\\$\\{(.*?)}");
		Matcher matcher = regex.matcher(pattern);

		StringBuilder result = new StringBuilder();
		int lastIndex = 0;
		while (matcher.find()) {
			String placeholder = matcher.group(1); // e.g., companyCode
			Object value = values.get(placeholder);

			// Extract separator text before placeholder
			String separator = pattern.substring(lastIndex, matcher.start());

			// Include only if value is not zero
			if (value != null && !(value instanceof Integer && (Integer) value == 0)) {
				result.append(separator).append(value);
			}

			lastIndex = matcher.end();
		}

		// Append trailing part after last placeholder
		result.append(pattern.substring(lastIndex));

		// Optional cleanup
		return result.toString().replaceAll("[-_/\\.]{2,}", "-") // prevent multiple symbols
				.replaceAll("^[-_/\\.]+|[-_/\\.]+$", ""); // trim ends
	}


}
