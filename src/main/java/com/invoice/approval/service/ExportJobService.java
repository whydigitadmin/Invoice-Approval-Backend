package com.invoice.approval.service;

import com.invoice.approval.dto.ExportJobRequestDTO;
import com.invoice.approval.entity.UTExportJob;
import java.util.List;
import java.util.Map;

public interface ExportJobService {
    Map<String, Object> createExportJob(ExportJobRequestDTO requestDTO);
    
//    List<UTExportJob> getAllExportJobs();
//    
//    UTExportJob getExportJobById(Long jobId);
//    
//    UTExportJob getExportJobByReferenceId(String referenceId);
}