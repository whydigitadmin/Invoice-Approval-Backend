package com.invoice.approval.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.ImportJobRequestDTO;
import com.invoice.approval.entity.UTImportJob;

@Service
public interface ImportJobService {

//    private static final Logger LOGGER = LoggerFactory.getLogger(ImportJobService.class);

    public Map<String, Object> createImportJob(ImportJobRequestDTO requestDTO);
//        LOGGER.info("Creating import job");
//        Map<String, Object> result = new HashMap<>();
//        result.put("message", "Import job created successfully");
//        result.put("importJob", new UTImportJob());
//        return result;
//    }

	List<UTImportJob> getAllImportJobs();

	UTImportJob getImportJobById(Long jobId);

	UTImportJob getImportJobByReferenceId(String referenceId);

//    public List<UTImportJob> getAllImportJobs() {
//        LOGGER.info("Getting all import jobs");
//        return List.of();
//    }
//
//    public UTImportJob getImportJobById(Long id) {
//        LOGGER.info("Getting import job by id: {}", id);
//        return new UTImportJob();
//    }
//
//    public UTImportJob getImportJobByReferenceId(String referenceId) {
//        LOGGER.info("Getting import job by referenceId: {}", referenceId);
//        return new UTImportJob();
//    }
}