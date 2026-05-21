package com.invoice.approval.service;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.approval.dto.ExportJobRequestDTO;
import com.invoice.approval.dto.ExportJobRequestDTO.CargoDetails;
import com.invoice.approval.dto.ExportJobRequestDTO.CargoInfo;
import com.invoice.approval.dto.ExportJobRequestDTO.HeaderDetails;
import com.invoice.approval.dto.ExportJobRequestDTO.InvoiceDetail;
import com.invoice.approval.dto.ExportJobRequestDTO.InvoiceInfo;
import com.invoice.approval.dto.ExportJobRequestDTO.Job;
import com.invoice.approval.dto.ExportJobRequestDTO.JobDetails;
import com.invoice.approval.dto.ExportJobRequestDTO.JobInfo;
import com.invoice.approval.dto.ExportJobRequestDTO.PartyDetails;
import com.invoice.approval.dto.ExportJobRequestDTO.ShipmentDetails;
import com.invoice.approval.dto.ExportJobRequestDTO.ShipmentInfo;
import com.invoice.approval.dto.ExportJobRequestDTO.TenantDetails;
import com.invoice.approval.dto.ExportJobRequestDTO.TenantInfo;
import com.invoice.approval.entity.UTExportJob;
import com.invoice.approval.repo.UTExportJobRepo;

@Service
public class ExportJobServiceImpl implements ExportJobService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportJobServiceImpl.class);
    
    @Autowired
    private UTExportJobRepo exportJobRepo;
    
    @Override
    @Transactional
    public Map<String, Object> createExportJob(ExportJobRequestDTO requestDTO) {
        String message;
        UTExportJob exportJob = null;
        
        LOGGER.info("========== STARTING EXPORT JOB CREATION ==========");
        
        // Get main job object
        Job job = requestDTO.getJob();
        if (job == null) {
            throw new RuntimeException("Job object is null in request");
        }
        
        // Get header details
        HeaderDetails headerDetails = job.getHeaderDetails();
        if (headerDetails == null) {
            throw new RuntimeException("HeaderDetails is null in request");
        }
        
        String jobNo = headerDetails.getJobNo();
        String referenceId = headerDetails.getReferenceId();
        
        // CRITICAL FIX: Handle empty or null referenceId
        if (referenceId == null || referenceId.trim().isEmpty()) {
            referenceId = jobNo;  // Use jobNo as referenceId
            LOGGER.warn("referenceId is empty/null, using jobNo as referenceId: {}", referenceId);
        }
        
        LOGGER.info("Processing export job with jobNo: {}, referenceId: {}", jobNo, referenceId);
        
        // Get shipment details and job details
        ShipmentDetails shipmentDetails = job.getShipmentDetails();
        if (shipmentDetails == null) {
            throw new RuntimeException("ShipmentDetails is null in request");
        }
        
        JobDetails jobDetails = shipmentDetails.getJobDetails();
        if (jobDetails == null) {
            throw new RuntimeException("JobDetails is null in request");
        }
        
        // Check for existing job by jobNo
        UTExportJob existingJob = exportJobRepo.findByJobNo(jobNo).orElse(null);
        
        if (existingJob == null) {
            exportJob = new UTExportJob();
            message = "Export Job created successfully";
            LOGGER.info("Creating NEW export job with jobNo: {}", jobNo);
        } else {
            exportJob = existingJob;
            message = "Export Job updated successfully";
            LOGGER.info("UPDATING existing export job with ID: {}, JobNo: {}", existingJob.getJobId(), existingJob.getJobNo());
        }
        
        try {
            // Map header details - ALWAYS set referenceId (never null)
            exportJob.setReferenceId(referenceId);  // This will never be null now
            exportJob.setOrganizationId(headerDetails.getOrganizationId());
            exportJob.setOrganizationBranchCode(headerDetails.getOrganizationBranchCode());
            exportJob.setOrganizationName(headerDetails.getOrganizationName());
            exportJob.setJobType(headerDetails.getJobType());
            exportJob.setTimeStamp(headerDetails.getTimeStamp());
            exportJob.setJobNo(jobNo);
        
            // Map tenant details
            TenantDetails tenants = jobDetails.getTenants();
            if (tenants != null && tenants.getCb() != null) {
                TenantInfo cb = tenants.getCb();
                exportJob.setTenantName(cb.getName());
                exportJob.setTenantBranch(cb.getBranch());
            }
            
            // ========== MAP JOB INFO ==========
            if (jobDetails.getJobInfo() != null) {
                JobInfo jobInfo = jobDetails.getJobInfo();
                
                // IMPORTANT: Set jobNo from header, not from jobInfo
                exportJob.setJobNo(jobNo);
                
                if (jobInfo.getJobType() != null && !jobInfo.getJobType().isEmpty()) {
                    exportJob.setJobInfoJobType(jobInfo.getJobType());
                }
                
                if (jobInfo.getMot() != null && !jobInfo.getMot().isEmpty()) {
                    exportJob.setMot(jobInfo.getMot());
                    LOGGER.info("✅ MOT set to: {}", jobInfo.getMot());
                }
                
                if (jobInfo.getCustomsHouseCode() != null && !jobInfo.getCustomsHouseCode().isEmpty()) {
                    exportJob.setCustomsHouseCode(jobInfo.getCustomsHouseCode());
                }
                
                if (jobInfo.getCustomerRefNo() != null && !jobInfo.getCustomerRefNo().isEmpty()) {
                    exportJob.setCustomerRefNo(jobInfo.getCustomerRefNo());
                }
            } else {
                // If jobInfo is null, still set the jobNo from header
                exportJob.setJobNo(jobNo);
            }
            
            // Map exporter details
            if (jobDetails.getExporter() != null) {
                PartyDetails exporter = jobDetails.getExporter();
                exportJob.setExporterName(exporter.getName());
                exportJob.setExporterPartyId(exporter.getPartyId());
                exportJob.setExporterPanNo(exporter.getPanNo());
                exportJob.setExporterAddress1(exporter.getAddress1());
                exportJob.setExporterAddress2(exporter.getAddress2());
                exportJob.setExporterCity(exporter.getCity());
                exportJob.setExporterPincode(exporter.getPincode());
                exportJob.setExporterState(exporter.getState());
                exportJob.setExporterCountry(exporter.getCountry());
            }
            
            // Map consignee details
            if (jobDetails.getConsignee() != null) {
                PartyDetails consignee = jobDetails.getConsignee();
                exportJob.setConsigneeName(consignee.getName());
                exportJob.setConsigneeBranch(consignee.getBranch());
                exportJob.setConsigneeAddress1(consignee.getAddress1());
                exportJob.setConsigneeAddress2(consignee.getAddress2());
                exportJob.setConsigneeCity(consignee.getCity());
                exportJob.setConsigneePincode(consignee.getPincode());
                exportJob.setConsigneeState(consignee.getState());
                exportJob.setConsigneeCountry(consignee.getCountry());
            }
            
            // Map shipment details
            if (jobDetails.getShipmentDetails() != null) {
                ShipmentInfo shipment = jobDetails.getShipmentDetails();
                exportJob.setPortOfLoading(shipment.getPortOfLoading());
                exportJob.setPortOfDischarge(shipment.getPortOfDischarge());
                exportJob.setCountryOfDischarge(shipment.getCountryOfDischarge());
                exportJob.setPortOfFinalDestination(shipment.getPortOfFinalDestination());
                exportJob.setCountryOfFinalDestination(shipment.getCountryOfFinalDestination());
                exportJob.setHawbHblNo(shipment.getHawbHblNo());
            }
            
            // Map cargo details
            if (jobDetails.getCargoDetails() != null) {
                CargoInfo cargo = jobDetails.getCargoDetails();
                
                if (cargo.getGrossWeight() != null && !cargo.getGrossWeight().isEmpty()) {
                    try {
                        exportJob.setGrossWeight(Double.parseDouble(cargo.getGrossWeight()));
                    } catch (NumberFormatException e) {
                        exportJob.setGrossWeight(0.0);
                    }
                }
                
                if (cargo.getNetWeight() != null && !cargo.getNetWeight().isEmpty()) {
                    try {
                        exportJob.setNetWeight(Double.parseDouble(cargo.getNetWeight()));
                    } catch (NumberFormatException e) {
                        exportJob.setNetWeight(0.0);
                    }
                }
                
                if (cargo.getChargeableWeight() != null && !cargo.getChargeableWeight().isEmpty()) {
                    try {
                        exportJob.setChargeableWeight(Double.parseDouble(cargo.getChargeableWeight()));
                    } catch (NumberFormatException e) {
                        exportJob.setChargeableWeight(0.0);
                    }
                }
                
                if (cargo.getUom() != null && !cargo.getUom().isEmpty()) {
                    exportJob.setWeightUom(cargo.getUom());
                }
                
                if (cargo.getNoOfPackages() != null && !cargo.getNoOfPackages().isEmpty()) {
                    try {
                        exportJob.setNoOfPackages(Integer.parseInt(cargo.getNoOfPackages()));
                    } catch (NumberFormatException e) {
                        exportJob.setNoOfPackages(0);
                    }
                }
                
                if (cargo.getPackageCode() != null && !cargo.getPackageCode().isEmpty()) {
                    exportJob.setPackageCode(cargo.getPackageCode());
                }
                
                if (cargo.getNoOfContainers() != null && !cargo.getNoOfContainers().isEmpty()) {
                    try {
                        exportJob.setNoOfContainers(Integer.parseInt(cargo.getNoOfContainers()));
                    } catch (NumberFormatException e) {
                        exportJob.setNoOfContainers(0);
                    }
                }
            }
            
            // Map invoice details from the list
            if (shipmentDetails.getInvoiceDetails() != null && !shipmentDetails.getInvoiceDetails().isEmpty()) {
                InvoiceDetail invoiceDetail = shipmentDetails.getInvoiceDetails().get(0);
                
                if (invoiceDetail.getInvoiceInfo() != null) {
                    InvoiceInfo invoiceInfo = invoiceDetail.getInvoiceInfo();
                    exportJob.setInvoiceNo(invoiceInfo.getInvoiceNo());
                    exportJob.setInvoiceDate(invoiceInfo.getInvoiceDate());
                    
                    if (invoiceInfo.getInvoiceValue() != null && !invoiceInfo.getInvoiceValue().isEmpty()) {
                        try {
                            exportJob.setInvoiceValue(Double.parseDouble(invoiceInfo.getInvoiceValue()));
                        } catch (NumberFormatException e) {
                            exportJob.setInvoiceValue(0.0);
                        }
                    }
                    
                    exportJob.setInvoiceCurrency(invoiceInfo.getInvoiceCurrency());
                    exportJob.setNatureOfContract(invoiceInfo.getNatureOfContract());
                }
                
                if (invoiceDetail.getCargoDetails() != null) {
                    CargoDetails cargoDetails = invoiceDetail.getCargoDetails();
                    exportJob.setInvoiceTotalGrossWeight(cargoDetails.getTotalGrossWeight());
                    exportJob.setInvoiceTotalNetWeight(cargoDetails.getTotalNetWeight());
                    exportJob.setInvoiceWeightUom(cargoDetails.getUom());
                    exportJob.setInvoiceNoOfPackages(cargoDetails.getNoOfPackages());
                    exportJob.setInvoicePackageCode(cargoDetails.getPackageCode());
                }
                
                if (invoiceDetail.getBuyer() != null) {
                    PartyDetails buyer = invoiceDetail.getBuyer();
                    exportJob.setInvoiceBuyerName(buyer.getName());
                    exportJob.setInvoiceBuyerAddress1(buyer.getAddress1());
                    exportJob.setInvoiceBuyerCity(buyer.getCity());
                }
                
                if (invoiceDetail.getExporter() != null) {
                    PartyDetails invoiceExporter = invoiceDetail.getExporter();
                    exportJob.setInvoiceExporterName(invoiceExporter.getName());
                }
            }
            
            // Set audit fields
            exportJob.setCreatedBy("SYSTEM");
            exportJob.setModifiedBy("SYSTEM");
            
            if (headerDetails.getTimeStamp() != null) {
                exportJob.setCreatedOn(headerDetails.getTimeStamp().toString());
                exportJob.setModifiedOn(headerDetails.getTimeStamp().toString());
            }
            
            // Save the entity
            exportJob = exportJobRepo.save(exportJob);
            LOGGER.info("✅ Saved export job with ID: {}, JobNo: {}", exportJob.getJobId(), exportJob.getJobNo());
            
        } catch (Exception e) {
            LOGGER.error("========== ERROR SAVING EXPORT JOB ==========");
            LOGGER.error("Error message: {}", e.getMessage());
            LOGGER.error("Full stack trace: ", e);
            throw new RuntimeException("Failed to save export job: " + e.getMessage(), e);
        }
        
        // Create response with full object
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("exportJob", exportJob);
        response.put("jobId", exportJob.getJobId());
        response.put("jobNo", exportJob.getJobNo());
        response.put("referenceId", exportJob.getReferenceId());
        response.put("mot", exportJob.getMot());
        
        LOGGER.info("✅ Export job processing completed for jobNo: {}", jobNo);
        
        return response;
    }
    
//    @Override
//    @Transactional
//    public UTExportJob getExportJobById(Long jobId) {
//        LOGGER.info("Fetching export job by ID: {}", jobId);
//        return exportJobRepo.findById(jobId)
//                .orElseThrow(() -> new EntityNotFoundException("Export Job not found with ID: " + jobId));
//    }
//    
//    @Override
//    @Transactional
//    public UTExportJob getExportJobByJobNo(String jobNo) {
//        LOGGER.info("Fetching export job by JobNo: {}", jobNo);
//        return exportJobRepo.findByJobNo(jobNo)
//                .orElseThrow(() -> new EntityNotFoundException("Export Job not found with JobNo: " + jobNo));
//    }
}