package com.invoice.approval.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.approval.dto.EmployeeMasterDTO;
import com.invoice.approval.entity.EmployeeMasterVO;
import com.invoice.approval.exception.ApplicationException;
import com.invoice.approval.repo.EmployeeMasterRepo;


@Service
public class EmployeeMasterServiceImpl implements EmployeeMasterService {

	@Autowired
	EmployeeMasterRepo empMasRepo;


	
	@Override
	public Map<String, Object> updateCreateEmpMaster(EmployeeMasterDTO empMasDTO) throws ApplicationException {
	    String message;
	    EmployeeMasterVO empMasVO;

	    if (empMasDTO.getId() == null || empMasDTO.getId() == 0) {
	        empMasVO = new EmployeeMasterVO();
	        empMasVO.setCreatedBy(empMasDTO.getCreatedBy());
	        message = "Employee Master Created Successfully";
	    } else {
	        empMasVO = empMasRepo.findById(empMasDTO.getId())
	            .orElseThrow(() -> new ApplicationException("Employee Master not found with id: " + empMasDTO.getId()));
	        message = "Employee Master Updated Successfully";
	    }

	    empMasVO.setUpdatedBy(empMasDTO.getCreatedBy());

	    empMasVO = getEmpMasterVOFromEmpMasterDTO(empMasVO, empMasDTO);
	    empMasRepo.save(empMasVO);

	    Map<String, Object> response = new HashMap<>();
	    response.put("message", message);
	    response.put("empMasVO", empMasVO);
	    return response;
	}
	
	private EmployeeMasterVO getEmpMasterVOFromEmpMasterDTO(EmployeeMasterVO empMasVO, EmployeeMasterDTO empMasDTO) {
	    empMasVO.setBranch(empMasDTO.getBranch()); // ✅ Fixed
	    empMasVO.setEmployee(empMasDTO.getEmployee());
	    empMasVO.setCode(empMasDTO.getCode());
	    empMasVO.setDob(empMasDTO.getDob());
	    empMasVO.setDoj(empMasDTO.getDoj());	    
	    empMasVO.setDepartment(empMasDTO.getDepartment());
	    empMasVO.setDesignation(empMasDTO.getDesignation());
	    empMasVO.setLvl(empMasDTO.getLvl());
	    empMasVO.setReportingto(empMasDTO.getReportingto());
	    empMasVO.setReportingtocode(empMasDTO.getReportingtocode());
	    empMasVO.setActive(empMasDTO.getActive());
	    empMasVO.setAttachment(empMasDTO.getAttachment());
	    empMasVO.setMailid(empMasDTO.getMailid());
	    empMasVO.setMobile(empMasDTO.getMobile());
	    empMasVO.setSubdepartment(empMasDTO.getSubdepartment());
	    empMasVO.setVertical(empMasDTO.getVertical());
	    empMasVO.setCostcenter(empMasDTO.getCostcenter());
	    empMasVO.setBranchhead(empMasDTO.getBranchhead());
	    empMasVO.setRegionalhead(empMasDTO.getRegionalhead());
	    empMasVO.setVerticalhead(empMasDTO.getVerticalhead());
	    empMasVO.setCorpteam(empMasDTO.getCorpteam());
	    return empMasVO;
	    
	}




	
	@Override
	public EmployeeMasterVO uploadImageInBloob(MultipartFile file, Long employeeMasterId) throws IOException {
	    EmployeeMasterVO empMasterVO = empMasRepo.findById(employeeMasterId)
	        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeMasterId));

	    // Check if a new file is uploaded (not empty or null)
	    if (file != null && !file.isEmpty()) {
	        empMasterVO.setAttachment(file.getBytes());
	    }

	    return empMasRepo.save(empMasterVO);
	}




	@Override
	public List<EmployeeMasterVO> getAllEmployees() {
		// TODO Auto-generated method stub
		return empMasRepo.findAll();
	}


	 @Transactional
	    @Override
	    public void excelUploadForEmployeeMaster(MultipartFile[] files, String createdBy) throws ApplicationException {
	        List<EmployeeMasterVO> dataToSave = new ArrayList<>();

	        for (MultipartFile file : files) {
	            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
	                Sheet sheet = workbook.getSheetAt(0);
	                Row headerRow = sheet.getRow(0);
	                
	                // 🆕 ADD DEBUG LOGGING
	                System.out.println("=== EXCEL UPLOAD DEBUG ===");
	                System.out.println("File: " + file.getOriginalFilename());
	                System.out.println("Sheet name: " + sheet.getSheetName());
	                System.out.println("Number of rows: " + sheet.getPhysicalNumberOfRows());

	                if (!isHeaderValid(headerRow)) {
	                    throw new ApplicationException("Invalid Excel format. Please refer to the sample file.");
	                }

	                for (Row row : sheet) {
	                    if (row.getRowNum() == 0 || isRowEmpty(row)) {
	                        continue; // Skip header and empty rows
	                    }

	                    try {
	                    	String branch = getStringCellValue(row.getCell(0));
	                        String employee = getStringCellValue(row.getCell(1));
	                        String code = getStringCellValue(row.getCell(2));
	                         LocalDate dob = getDateCellValue(row.getCell(3));
	                         LocalDate doj = getDateCellValue(row.getCell(4));
	                        String department = getStringCellValue(row.getCell(5));
	                        String designation = getStringCellValue(row.getCell(6));
	                        String lvl = getStringCellValue(row.getCell(7));
	                        String reportingTo = getStringCellValue(row.getCell(8));
	                        String reportingToCode = getStringCellValue(row.getCell(9));
	                        String active = getStringCellValue(row.getCell(10));
	                        String mailid = getStringCellValue(row.getCell(11));
	                        String mobile = getStringCellValue(row.getCell(12));
	                        String subdepartment = getStringCellValue(row.getCell(13));
	                        String vertical = getStringCellValue(row.getCell(14));
	                        String costcenter = getStringCellValue(row.getCell(15));
	                        String branchhead = getStringCellValue(row.getCell(16));
	                        String regionalhead = getStringCellValue(row.getCell(17));
	                        String verticalhead = getStringCellValue(row.getCell(18));
	                        String corpteam = getStringCellValue(row.getCell(19));

	                       // ✅ Check for duplicate employee code
	if (empMasRepo.existsByCode(code)) {
	    throw new ApplicationException("Duplicate empCode found in DB: " + code + " at row " + (row.getRowNum() + 1));
	}


	                        EmployeeMasterVO dataVO = new EmployeeMasterVO();
	                        dataVO.setBranch(branch);	                        dataVO.setEmployee(employee);
	                        dataVO.setCode(code);
	                       dataVO.setDob(dob);
	                       dataVO.setDoj(doj);
	                        dataVO.setDepartment(department);
	                        dataVO.setDesignation(designation);
	                        dataVO.setLvl(lvl);
	                        dataVO.setReportingto(reportingTo);
	                        dataVO.setReportingtocode(reportingToCode);
	                        dataVO.setActive(active);
	                        dataVO.setCreatedBy(createdBy);
	                        dataVO.setUpdatedBy(createdBy);
	                        dataVO.setMailid(mailid);
	                        dataVO.setMobile(mobile);
	                        dataVO.setSubdepartment(subdepartment);
	                        dataVO.setVertical(vertical);
	                        dataVO.setCostcenter(costcenter);
	                        dataVO.setBranchhead(branchhead);
	                        dataVO.setRegionalhead(regionalhead);
	                        dataVO.setVerticalhead(verticalhead);
	                        dataVO.setCorpteam(corpteam);
	                        

	                        dataToSave.add(dataVO);

	                    } catch (Exception e) {
	                        e.printStackTrace(); // Optionally log or collect error rows
	                    }
	                }

	                // ✅ Save valid rows to DB
	                empMasRepo.saveAll(dataToSave);

	            } catch (IOException | EncryptedDocumentException e) {
	                throw new ApplicationException("Failed to process file: " + file.getOriginalFilename(), e);
	            }
	        }
	    }

	    // ---------- Utility Methods ----------

	 private LocalDate getDateCellValue(Cell cell) {
		    if (cell == null || cell.getCellType() == CellType.BLANK) {
		        return null;
		    }

		    if (DateUtil.isCellDateFormatted(cell)) {
		        return cell.getDateCellValue().toInstant()
		            .atZone(java.time.ZoneId.systemDefault())
		            .toLocalDate();
		    }

		    throw new IllegalArgumentException("Invalid date format in Excel at cell: " + cell.getAddress());
		}


		private boolean isHeaderValid(Row headerRow) {
	        return headerRow != null
	        		&& "branch".equalsIgnoreCase(getStringCellValue(headerRow.getCell(0)))
	            && "employee".equalsIgnoreCase(getStringCellValue(headerRow.getCell(1)))
	            && "code".equalsIgnoreCase(getStringCellValue(headerRow.getCell(2)))
	            && "dob".equalsIgnoreCase(getStringCellValue(headerRow.getCell(3)))
	            && "doj".equalsIgnoreCase(getStringCellValue(headerRow.getCell(4)))
	            && "department".equalsIgnoreCase(getStringCellValue(headerRow.getCell(5)))
	            && "designation".equalsIgnoreCase(getStringCellValue(headerRow.getCell(6)))
	            && "lvl".equalsIgnoreCase(getStringCellValue(headerRow.getCell(7)))
	            && "reportingto".equalsIgnoreCase(getStringCellValue(headerRow.getCell(8)))
	            && "reportingtocode".equalsIgnoreCase(getStringCellValue(headerRow.getCell(9)))
	            && "active".equalsIgnoreCase(getStringCellValue(headerRow.getCell(10)))
	        && "mailid".equalsIgnoreCase(getStringCellValue(headerRow.getCell(11)))
	        && "mobile".equalsIgnoreCase(getStringCellValue(headerRow.getCell(12)))
	        
	        && "subdepartment".equalsIgnoreCase(getStringCellValue(headerRow.getCell(13)))
	        && "vertical".equalsIgnoreCase(getStringCellValue(headerRow.getCell(14)))
	        && "costcenter".equalsIgnoreCase(getStringCellValue(headerRow.getCell(15)))
	        && "branchhead".equalsIgnoreCase(getStringCellValue(headerRow.getCell(16)))
	        && "regionalhead".equalsIgnoreCase(getStringCellValue(headerRow.getCell(17)))
	        && "verticalhead".equalsIgnoreCase(getStringCellValue(headerRow.getCell(18)))
	        && "corpteam".equalsIgnoreCase(getStringCellValue(headerRow.getCell(19)));
	        
	    }

	    private boolean isRowEmpty(Row row) {
	        for (Cell cell : row) {
	            if (cell != null && cell.getCellType() != CellType.BLANK) {
	                return false;
	            }
	        }
	        return true;
	    }

	    private String getStringCellValue(Cell cell) {
	        if (cell == null) return "";
	        switch (cell.getCellType()) {
	            case STRING: return cell.getStringCellValue().trim();
	            case NUMERIC:
	                if (DateUtil.isCellDateFormatted(cell)) {
	                	
	                    return new SimpleDateFormat("dd-MM-yyyy").format(cell.getDateCellValue());
	                }
	                return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
	            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
	            case FORMULA: return cell.getCellFormula();
	            default: return "";
	        }
	    }

	    private boolean getBooleanCellValue(Cell cell) {
	        if (cell == null) return false;
	        if (cell.getCellType() == CellType.BOOLEAN) {
	            return cell.getBooleanCellValue();
	        }
	        String value = getStringCellValue(cell).toLowerCase();
	        return value.equals("true") || value.equals("yes") || value.equals("1");
	    }

		@Override
		public int getTotalRows() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getSuccessfulUploads() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
 


