package com.invoice.approval.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoice.approval.dto.ImportJobRequestDTO;
import com.invoice.approval.entity.SentinelRawJsonData;
import com.invoice.approval.entity.UTImportJob;
import com.invoice.approval.entity.UTImportJobBond;
import com.invoice.approval.entity.UTImportJobCertificate;
import com.invoice.approval.entity.UTImportJobContainer;
import com.invoice.approval.entity.UTImportJobHss;
import com.invoice.approval.entity.UTImportJobInvoice;
import com.invoice.approval.entity.UTImportJobLineItem;
import com.invoice.approval.entity.UTImportJobShipment;
import com.invoice.approval.entity.UTImportJobStatement;
import com.invoice.approval.entity.UTImportJobSupportingDoc;
import com.invoice.approval.entity.UTImportJobTenant;
import com.invoice.approval.repo.SentinelJsonRepo;
import com.invoice.approval.repo.UTImportJobBondRepo;
import com.invoice.approval.repo.UTImportJobCertificateRepo;
import com.invoice.approval.repo.UTImportJobContainerRepo;
import com.invoice.approval.repo.UTImportJobHssRepo;
import com.invoice.approval.repo.UTImportJobInvoiceRepo;
import com.invoice.approval.repo.UTImportJobLineItemRepo;
import com.invoice.approval.repo.UTImportJobRepo;
import com.invoice.approval.repo.UTImportJobShipmentRepo;
import com.invoice.approval.repo.UTImportJobStatementRepo;
import com.invoice.approval.repo.UTImportJobSupportingDocRepo;
import com.invoice.approval.repo.UTImportJobTenantRepo;

@Service
public class ImportJobServiceImpl implements ImportJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportJobServiceImpl.class);

    @Autowired
    private UTImportJobRepo importJobRepo;

    @Autowired
    private UTImportJobTenantRepo tenantRepo;

    @Autowired
    private UTImportJobShipmentRepo shipmentRepo;

    @Autowired
    private UTImportJobContainerRepo containerRepo;

    @Autowired
    private UTImportJobInvoiceRepo invoiceRepo;

    @Autowired
    private UTImportJobLineItemRepo lineItemRepo;

    @Autowired
    private UTImportJobBondRepo bondRepo;

    @Autowired
    private UTImportJobCertificateRepo certificateRepo;

    @Autowired
    private UTImportJobHssRepo hssRepo;

    @Autowired
    private UTImportJobStatementRepo statementRepo;

    @Autowired
    private UTImportJobSupportingDocRepo supportingDocRepo;

    @Autowired
    private SentinelJsonRepo sentinelRawJsonDataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public Map<String, Object> createImportJob(ImportJobRequestDTO requestDTO) {
        String message;
        UTImportJob importJob = null;

        // Validation
        if (requestDTO == null || requestDTO.getJob() == null || requestDTO.getJob().getHeaderDetails() == null) {
            throw new RuntimeException("Invalid request: Missing required data");
        }

        String jobNo = requestDTO.getJob().getHeaderDetails().getJobNo();  // Changed from referenceId
        String referenceId = requestDTO.getJob().getHeaderDetails().getReferenceId();
        LOGGER.info("========== STARTING IMPORT JOB CREATION ==========");
        LOGGER.info("Processing import job with jobNo: {}, referenceId: {}", jobNo, referenceId);  // Updated log

        // Save raw JSON to Sentinel
//        saveRawJsonToSentinel(requestDTO, referenceId);

        // Check for existing job by jobNo instead of referenceId
        UTImportJob existingJob = importJobRepo.findByJobNo(jobNo).orElse(null);  // CHANGED HERE

        if (existingJob == null) {
            importJob = new UTImportJob();
            message = "Import Job created successfully";
            LOGGER.info("Creating NEW import job with jobNo: {}", jobNo);  // Updated log
        } else {
            importJob = existingJob;
            message = "Import Job updated successfully";
            LOGGER.info("UPDATING existing import job with ID: {}, JobNo: {}", existingJob.getJobId(), existingJob.getJobNo());
            // Delete child records for update scenario
            deleteChildRecords(importJob.getJobId());
        }

        try {
            // Map and save main entity
            importJob = mapToImportJobEntity(importJob, requestDTO);
            importJob = importJobRepo.save(importJob);
            LOGGER.info("✅ Saved main import job with ID: {}, JobNo: {}", importJob.getJobId(), importJob.getJobNo());

            // CRITICAL: Check if JobId is present before saving children
            if (importJob.getJobId() == null) {
                throw new RuntimeException("Job ID is null after saving main entity!");
            }

            // Save child entities
            saveTenants(requestDTO, importJob.getJobId());
            saveShipmentsAndContainers(requestDTO, importJob.getJobId());
            saveInvoicesAndLineItems(requestDTO, importJob.getJobId());
            saveBonds(requestDTO, importJob.getJobId());
            saveCertificates(requestDTO, importJob.getJobId());
            saveHssList(requestDTO, importJob.getJobId());
            saveStatements(requestDTO, importJob.getJobId());
            saveSupportingDocs(requestDTO, importJob.getJobId());

            LOGGER.info("✅ Successfully completed processing for jobNo: {}", jobNo);  // Updated log
            
        } catch (Exception e) {
            LOGGER.error("========== ERROR SAVING IMPORT JOB ==========");
            LOGGER.error("Error message: {}", e.getMessage());
            LOGGER.error("Error cause: ", e.getCause());
            LOGGER.error("Full stack trace: ", e);
            throw new RuntimeException("Failed to save import job: " + e.getMessage(), e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("importJob", importJob);
        return response;
    }
    private void deleteChildRecords(Long jobId) {
        LOGGER.info("Deleting child records for jobId: {}", jobId);
        try {
            tenantRepo.deleteByJobId(jobId);
            shipmentRepo.deleteByJobId(jobId);
//            containerRepo.deleteByJobId(jobId);
            invoiceRepo.deleteByJobId(jobId);
//            lineItemRepo.deleteByJobId(jobId);
            bondRepo.deleteByJobId(jobId);
            certificateRepo.deleteByJobId(jobId);
            hssRepo.deleteByJobId(jobId);
            statementRepo.deleteByJobId(jobId);
            supportingDocRepo.deleteByJobId(jobId);
            LOGGER.info("✅ Deleted existing child records for jobId: {}", jobId);
        } catch (Exception e) {
            LOGGER.warn("Error deleting child records: {}", e.getMessage());
        }
    }

    private void saveRawJsonToSentinel(ImportJobRequestDTO requestDTO, String jobNo) {
        try {
            // Check if already exists by jobNo
            Optional<SentinelRawJsonData> existing = sentinelRawJsonDataRepository.findByJobNo(jobNo);
            if (existing.isPresent()) {
                LOGGER.info("Raw JSON already exists for jobNo: {}, skipping save", jobNo);
                return;
            }
            
            String jsonPayload = objectMapper.writeValueAsString(requestDTO);
            SentinelRawJsonData sentinelData = new SentinelRawJsonData();
            sentinelData.setJsonPayload(jsonPayload);
            sentinelData.setDataType("IMPORT_JOB");
            sentinelData.setJobNo(jobNo);  // Use jobNo instead of referenceId
            sentinelData.setReferenceId(requestDTO.getJob().getHeaderDetails().getReferenceId());
            sentinelData.setInsertedFlag("F");
            sentinelRawJsonDataRepository.save(sentinelData);
            LOGGER.info("✅ Saved raw JSON to Sentinel table");
        } catch (Exception e) {
            LOGGER.error("Error saving to Sentinel table: {}", e.getMessage());
        }
    }

    private UTImportJob mapToImportJobEntity(UTImportJob entity, ImportJobRequestDTO requestDTO) {
    	 ImportJobRequestDTO.HeaderDetails header = requestDTO.getJob().getHeaderDetails();
    	    ImportJobRequestDTO.ShipmentDetailsWrapper shipmentDetails = requestDTO.getJob().getShipmentDetails();

    	    // ===== HEADER FIELDS =====
    	    String referenceId = header.getReferenceId();
    	    if (referenceId == null || referenceId.trim().isEmpty()) {
    	        referenceId = header.getJobNo();  // Use jobNo as referenceId
    	        LOGGER.warn("referenceId is empty, using jobNo as referenceId: {}", referenceId);
    	    }
    	    entity.setReferenceId(referenceId);
//    	    entity.setReferenceId(header.getReferenceId());
    	    entity.setOrganizationId(header.getOrganizationId());
    	    entity.setOrganizationBranchCode(header.getOrganizationBranchCode());
    	    entity.setOrganizationName(header.getOrganizationName());
    	    entity.setJobType(header.getJobType());
    	    entity.setTimeStamp(parseDateSafe(header.getTimeStamp()));
    	    entity.setJobNo(header.getJobNo());  
        LOGGER.info("Mapped Header - JobNo: {}, ReferenceId: {}", header.getJobNo(), header.getReferenceId());

        if (shipmentDetails != null && shipmentDetails.getJobDetails() != null) {
            ImportJobRequestDTO.JobDetails jobDetails = shipmentDetails.getJobDetails();
            
            // ===== IMPORTER FIELDS =====
            if (jobDetails.getImporter() != null) {
                ImportJobRequestDTO.Importer importer = jobDetails.getImporter();
                entity.setImporterName(safeSubstring(importer.getName(), 200));
                entity.setImporterPartyId(safeSubstring(importer.getPartyId(), 50));
                entity.setImporterIec(safeSubstring(importer.getIec(), 50));
                entity.setImporterBranch(safeSubstring(importer.getBranch(), 10));
                entity.setImporterPanNo(safeSubstring(importer.getPanNo(), 20));
                entity.setImporterAddress1(safeSubstring(importer.getAddress1(), 4000));
                entity.setImporterAddress2(safeSubstring(importer.getAddress2(), 4000));
                entity.setImporterCity(safeSubstring(importer.getCity(), 100));
                entity.setImporterPincode(safeSubstring(importer.getPincode(), 20));
                entity.setImporterState(safeSubstring(importer.getState(), 100));
                entity.setImporterCountry(safeSubstring(importer.getCountry(), 50));
                entity.setImporterType(safeSubstring(importer.getType(), 20));
                entity.setImporterAdCode(safeSubstring(importer.getAdCode(), 100));
                entity.setImporterIcegateId(safeSubstring(importer.getIcegateId(), 50));
                LOGGER.info("✅ Mapped Importer - Name: {}, IEC: {}", importer.getName(), importer.getIec());
            }

            // ===== JOB INFO FIELDS =====
            if (jobDetails.getJobInfo() != null) {
                ImportJobRequestDTO.JobInfo jobInfo = jobDetails.getJobInfo();
                entity.setBeNo(safeSubstring(jobInfo.getBeNo(), 50));
                entity.setBeDate(parseDateSafe(jobInfo.getBeDate()));
                entity.setBeType(safeSubstring(jobInfo.getBeType(), 50));
                entity.setCustomsHouseCode(safeSubstring(jobInfo.getCustomsHouseCode(), 50));
                entity.setMot(safeSubstring(jobInfo.getMot(), 20));
                entity.setCustomerRefNo(safeSubstring(jobInfo.getCustomerRefNo(), 100));
                entity.setPortOfOrigin(safeSubstring(jobInfo.getPortOfOrigin(), 50));
                entity.setCountryOfOrigin(safeSubstring(jobInfo.getCountryOfOrigin(), 50));
                entity.setUcrNo(safeSubstring(jobInfo.getUcrNo(), 50));
                entity.setUcrType(safeSubstring(jobInfo.getUcrType(), 20));
                entity.setRemarks(jobInfo.getRemarks());
                entity.setPaymentMethodCode(safeSubstring(jobInfo.getPaymentMethodCode(), 20));
                entity.setTotalAssessableValue(parseDoubleSafe(jobInfo.getTotalAssessableValue()));
                entity.setTotalDuty(parseDoubleSafe(jobInfo.getTotalDuty()));
                LOGGER.info("✅ Mapped JobInfo - BE No: {}, Total Duty: {}", jobInfo.getBeNo(), jobInfo.getTotalDuty());
            }

            // ===== SUPPLIER FIELDS =====
            if (jobDetails.getSupplier() != null) {
                ImportJobRequestDTO.Supplier supplier = jobDetails.getSupplier();
                entity.setSupplierName(safeSubstring(supplier.getName(), 200));
                entity.setSupplierPartyId(safeSubstring(supplier.getPartyId(), 50));
                String fullAddress = supplier.getAddress1();
                if (supplier.getAddress2() != null) fullAddress += " " + supplier.getAddress2();
                if (supplier.getAddress3() != null) fullAddress += " " + supplier.getAddress3();
                entity.setSupplierAddress(fullAddress);
                entity.setSupplierCity(safeSubstring(supplier.getCity(), 100));
                entity.setSupplierCountry(safeSubstring(supplier.getCountry(), 50));
            }

            // ===== EXCHANGE RATE FIELDS =====
            if (jobDetails.getExchangeRates() != null && !jobDetails.getExchangeRates().isEmpty()) {
                ImportJobRequestDTO.ExchangeRate exRate = jobDetails.getExchangeRates().get(0);
                entity.setCurrencyCode(safeSubstring(exRate.getCurrencyCode(), 50));
                entity.setExchangeRate(parseDoubleSafe(exRate.getRate()));
                entity.setExRateEffDate(parseDateSafe(exRate.getEffectiveDate()));
                entity.setCertificateNo(safeSubstring(exRate.getCertificateNo(), 100));
            }

            // ===== WAREHOUSE FIELDS =====
            if (jobDetails.getWarehouse() != null) {
                ImportJobRequestDTO.Warehouse warehouse = jobDetails.getWarehouse();
                entity.setWarehouseBeNo(safeSubstring(warehouse.getBeNo(), 50));
                entity.setWarehouseBeDate(parseDateSafe(warehouse.getBeDate()));
                entity.setWarehouseJobNo(safeSubstring(warehouse.getJobNo(), 50));
                entity.setWarehouseCode(safeSubstring(warehouse.getCode(), 100));
                entity.setWarehouseCustomsSiteId(safeSubstring(warehouse.getCustomsSiteId(), 50));
                entity.setWarehousePackagesReleased(safeSubstring(warehouse.getPackagesReleased(), 50));
                entity.setWarehousePackageCode(safeSubstring(warehouse.getPackageCode(), 20));
                entity.setWarehouseGrossWeight(safeSubstring(warehouse.getGrossWeight(), 50));
                entity.setWarehouseUom(safeSubstring(warehouse.getUom(), 10));
            }

            // ===== COMMERCIAL TAX FIELDS =====
            if (jobDetails.getCommercialTax() != null) {
                ImportJobRequestDTO.CommercialTax commercialTax = jobDetails.getCommercialTax();
                entity.setCommercialTaxRegNo(safeSubstring(commercialTax.getRegNo(), 50));
                entity.setCommercialTaxType(safeSubstring(commercialTax.getType(), 50));
                entity.setCommercialTaxState(safeSubstring(commercialTax.getState(), 10));
            }
        }
        return entity;
    }

    private void saveTenants(ImportJobRequestDTO requestDTO, Long jobId) {
        try {
            LOGGER.info("Starting to save tenants for jobId: {}", jobId);
            
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails().getTenants() != null) {
                
                ImportJobRequestDTO.Tenants tenants = requestDTO.getJob().getShipmentDetails().getJobDetails().getTenants();
                
                // Save CB Tenant
                if (tenants.getCb() != null) {
                    ImportJobRequestDTO.Cb cb = tenants.getCb();
                    
                    UTImportJobTenant tenant = new UTImportJobTenant();
                    tenant.setJobId(jobId);
                    tenant.setTenantType("CB");
                    tenant.setName(safeSubstring(cb.getName(), 200));
                    tenant.setBranch(safeSubstring(cb.getBranch(), 10));
                    tenant.setLicenseNo(safeSubstring(cb.getLicenseNo(), 50));
                    tenant.setAddressLine(cb.getAddressLine());
                    tenant.setCity(safeSubstring(cb.getCity(), 100));
                    tenant.setPincode(safeSubstring(cb.getPincode(), 20));
                    tenant.setState(safeSubstring(cb.getState(), 100));
                    tenant.setCountry(safeSubstring(cb.getCountry(), 50));
                    tenant.setIcegateId(safeSubstring(cb.getIcegateId(), 50));
                    tenant.setCustomerBranch(safeSubstring(cb.getCustomerBranch(), 50));
                    tenant.setSubmittedBy(safeSubstring(cb.getSubmittedBy(), 100));
                    tenant.setAeoRegNo(safeSubstring(cb.getAeoRegNo(), 50));
                    tenant.setAeoRole(safeSubstring(cb.getAeoRole(), 50));
                    tenant.setTransactionNo(safeSubstring(cb.getTransactionNo(), 200));
                    
                    tenantRepo.save(tenant);
                    LOGGER.info("✅ Saved CB tenant: {}", cb.getName());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save tenant: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save tenants", e);
        }
    }

    private void saveShipmentsAndContainers(ImportJobRequestDTO requestDTO, Long jobId) {
        try {
            LOGGER.info("Starting to save shipments for jobId: {}", jobId);
            
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails().getIgmDetails() != null) {
                
                for (ImportJobRequestDTO.IgmDetail igmDetail : requestDTO.getJob().getShipmentDetails().getJobDetails().getIgmDetails()) {
                    if (igmDetail.getIgm() != null) {
                        ImportJobRequestDTO.Igm igm = igmDetail.getIgm();
                        
                        UTImportJobShipment shipment = new UTImportJobShipment();
                        shipment.setJobId(jobId);
                        shipment.setIgmNo(safeSubstring(igm.getIgmNo(), 50));
                        shipment.setIgmDate(parseDateSafe(igm.getIgmDate()));
                        shipment.setGatewayNo(safeSubstring(igm.getGatewayNo(), 50));
                        shipment.setGatewayDate(parseDateSafe(igm.getGatewayDate()));
                        shipment.setGatewayPort(safeSubstring(igm.getGatewayPort(), 100));
                        shipment.setMawbMblNo(safeSubstring(igm.getMawbMblNo(), 50));
                        shipment.setMawbMblDate(parseDateSafe(igm.getMawbMblDate()));
                        shipment.setHawbHblNo(safeSubstring(igm.getHawbHblNo(), 50));
                        shipment.setHawbHblDate(parseDateSafe(igm.getHawbHblDate()));
                        shipment.setTotalNoOfPackages(safeSubstring(igm.getTotalNoOfPackages(), 50));
                        shipment.setPackageCode(safeSubstring(igm.getPackageCode(), 20));
                        shipment.setGrossWeight(safeSubstring(igm.getGrossWeight(), 50));
                        shipment.setWeightUom(safeSubstring(igm.getUom(), 10));
                        shipment.setMarksAndNos1(safeSubstring(igm.getMarksAndNos1(), 500));
                        shipment.setMarksAndNos2(safeSubstring(igm.getMarksAndNos2(), 500));
                        shipment.setMarksAndNos3(safeSubstring(igm.getMarksAndNos3(), 500));
                        shipment.setInwardDate(parseDateSafe(igm.getInwardDate()));
                        shipment.setCargoType(safeSubstring(igm.getCargoType(), 50));
                        shipment.setEta(parseDateSafe(igm.getEta()));
                        shipment.setBkVesselFlt(safeSubstring(igm.getBkVesselFlt(), 50));
                        shipment.setBkVoyage(safeSubstring(igm.getBkVoyage(), 50));
                        shipment.setCarrierCode(safeSubstring(igm.getCarrierCode(), 50));
                        shipment.setChargeableWeight(safeSubstring(igm.getChargeableWeight(), 50));
                        
                        UTImportJobShipment savedShipment = shipmentRepo.save(shipment);
                        LOGGER.info("✅ Saved shipment with ID: {}", savedShipment.getShipmentId());
                        
                        // Save Containers
                        if (igmDetail.getContainers() != null && !igmDetail.getContainers().isEmpty()) {
                            for (ImportJobRequestDTO.Container containerDTO : igmDetail.getContainers()) {
                                UTImportJobContainer container = new UTImportJobContainer();
                                container.setJobId(jobId);
                                container.setShipmentId(savedShipment.getShipmentId());
                                container.setContainerNo(safeSubstring(containerDTO.getContainerNo(), 50));
                                container.setSealNo(safeSubstring(containerDTO.getSealNo(), 50));
                                container.setLclFcl(safeSubstring(containerDTO.getLclFcl(), 10));
                                container.setType(safeSubstring(containerDTO.getType(), 50));
                                container.setTruckNo(safeSubstring(containerDTO.getTruckNo(), 50));
                                
                                containerRepo.save(container);
                                LOGGER.info("✅ Saved container: {}", containerDTO.getContainerNo());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save shipments/containers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save shipments and containers", e);
        }
    }

    private void saveInvoicesAndLineItems(ImportJobRequestDTO requestDTO, Long jobId) {
        try {
            LOGGER.info("Starting to save invoices for jobId: {}", jobId);
            
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getInvoiceDetails() != null) {
                
                for (ImportJobRequestDTO.InvoiceDetail invoiceDetail : requestDTO.getJob().getShipmentDetails().getInvoiceDetails()) {
                    if (invoiceDetail.getInvoiceInfo() != null) {
                        ImportJobRequestDTO.InvoiceInfo invoiceInfo = invoiceDetail.getInvoiceInfo();
                        
                        UTImportJobInvoice invoice = new UTImportJobInvoice();
                        invoice.setJobId(jobId);
                        invoice.setInvoiceNo(safeSubstring(invoiceInfo.getInvoiceNo(), 50));
                        invoice.setInvoiceDate(parseDateSafe(invoiceInfo.getInvoiceDate()));
                        invoice.setInvoiceValue(parseDoubleSafe(invoiceInfo.getInvoiceValue()));
                        invoice.setInvoiceCurrency(safeSubstring(invoiceInfo.getInvoiceCurrency(), 50));
                        invoice.setTermsOfInvoice(safeSubstring(invoiceInfo.getTermsOfInvoice(), 20));
                        invoice.setTotalAssessableValue(parseDoubleSafe(invoiceInfo.getTotalAssessableValue()));
                        invoice.setTotalDuty(parseDoubleSafe(invoiceInfo.getTotalDuty()));
                        invoice.setNatureOfTransaction(safeSubstring(invoiceInfo.getNatureOfTransaction(), 50));
                        invoice.setValuationMethod(safeSubstring(invoiceInfo.getValuationMethod(), 100));
                        invoice.setPaymentTerms(safeSubstring(invoiceInfo.getPaymentTerms(), 50));
                        invoice.setLcNo(safeSubstring(invoiceInfo.getLcNo(), 50));
                        invoice.setLcDate(parseDateSafe(invoiceInfo.getLcDate()));
                        invoice.setPoNo(safeSubstring(invoiceInfo.getPoNo(), 50));
                        invoice.setPoDate(parseDateSafe(invoiceInfo.getPoDate()));
                        invoice.setContractNo(safeSubstring(invoiceInfo.getContractNo(), 50));
                        invoice.setContractDate(parseDateSafe(invoiceInfo.getContractDate()));
                        invoice.setTotalItemValue(parseDoubleSafe(invoiceInfo.getTotalItemValue()));
                        
                        // Set SVB data
                        if (invoiceDetail.getSvb() != null) {
                            ImportJobRequestDTO.Svb svb = invoiceDetail.getSvb();
                            invoice.setSvbIsRelated(safeSubstring(svb.getIsRelated(), 10));
                            invoice.setSvbRefNo(safeSubstring(svb.getRefNo(), 50));
                            invoice.setSvbDate(parseDateSafe(svb.getDate()));
                            invoice.setSvbFlag(safeSubstring(svb.getFlag(), 50));
                            invoice.setSvbLoadOnAssessableValue(parseDoubleSafe(svb.getLoadOnAssessableValue()));
                            invoice.setWhetherLoadOnAssessable(safeSubstring(svb.getWhetherLoadOnAssessable(), 20));
                        }
                        
                        // Set Supplier
                        if (invoiceDetail.getSupplier() != null) {
                            invoice.setInvSupplierName(safeSubstring(invoiceDetail.getSupplier().getName(), 200));
                            invoice.setInvSupplierPartyId(safeSubstring(invoiceDetail.getSupplier().getPartyId(), 50));
                            invoice.setInvSupplierAddress1(invoiceDetail.getSupplier().getAddress1());
                            invoice.setInvSupplierCity(safeSubstring(invoiceDetail.getSupplier().getCity(), 100));
                        }
                        
                        // Set Buyer
                        if (invoiceDetail.getBuyer() != null) {
                            invoice.setInvBuyerName(safeSubstring(invoiceDetail.getBuyer().getName(), 200));
                            invoice.setInvBuyerPartyId(safeSubstring(invoiceDetail.getBuyer().getPartyId(), 50));
                            invoice.setInvBuyerAddress1(invoiceDetail.getBuyer().getAddress1());
                            invoice.setInvBuyerCity(safeSubstring(invoiceDetail.getBuyer().getCity(), 100));
                            invoice.setInvBuyerCountry(safeSubstring(invoiceDetail.getBuyer().getCountry(), 50));
                            invoice.setInvBuyerIcegateId(safeSubstring(invoiceDetail.getBuyer().getIcegateId(), 50));
                        }
                        
                        // Store charges as JSON
                        if (invoiceDetail.getCharges() != null) {
                            try {
                                String chargesJson = objectMapper.writeValueAsString(invoiceDetail.getCharges());
                                invoice.setChargesJson(chargesJson);
                            } catch (Exception e) {
                                LOGGER.warn("Failed to serialize charges JSON: {}", e.getMessage());
                            }
                        }
                        
                        // Store subCharges as JSON
                        if (invoiceDetail.getSubCharges() != null) {
                            try {
                                String subChargesJson = objectMapper.writeValueAsString(invoiceDetail.getSubCharges());
                                invoice.setSubChargesJson(subChargesJson);
                            } catch (Exception e) {
                                LOGGER.warn("Failed to serialize subCharges JSON: {}", e.getMessage());
                            }
                        }
                        
                        UTImportJobInvoice savedInvoice = invoiceRepo.save(invoice);
                        LOGGER.info("✅ Saved invoice: {}", invoiceInfo.getInvoiceNo());
                        
                        // Save Line Items
                        if (invoiceDetail.getLineItems() != null && !invoiceDetail.getLineItems().isEmpty()) {
                            for (ImportJobRequestDTO.LineItem lineItem : invoiceDetail.getLineItems()) {
                                if (lineItem.getLineItemInfo() != null) {
                                    ImportJobRequestDTO.LineItemInfo itemInfo = lineItem.getLineItemInfo();
                                    
                                    UTImportJobLineItem lineItemEntity = new UTImportJobLineItem();
                                    lineItemEntity.setJobId(jobId);
                                    lineItemEntity.setInvoiceId(savedInvoice.getInvoiceId());
                                    lineItemEntity.setPartCode(safeSubstring(itemInfo.getPartCode(), 100));
                                    lineItemEntity.setInvoiceDescription(safeSubstring(itemInfo.getInvoiceDescription(), 2000));
                                    lineItemEntity.setCustomsDescription(safeSubstring(itemInfo.getCustomsDescription(), 2000));
                                    lineItemEntity.setHsn(safeSubstring(itemInfo.getHsn(), 20));
                                    lineItemEntity.setBrand(safeSubstring(itemInfo.getBrand(), 100));
                                    lineItemEntity.setModel(safeSubstring(itemInfo.getModel(), 100));
                                    lineItemEntity.setCountryOfOrigin(safeSubstring(itemInfo.getCountryOfOrigin(), 50));
                                    lineItemEntity.setQuantity(parseDoubleSafe(itemInfo.getQty()));
                                    lineItemEntity.setUom(safeSubstring(itemInfo.getUom(), 20));
                                    lineItemEntity.setCusQuantity(parseDoubleSafe(itemInfo.getCusQty()));
                                    lineItemEntity.setCusUom(safeSubstring(itemInfo.getCusUom(), 20));
                                    lineItemEntity.setUnitPrice(parseDoubleSafe(itemInfo.getUnitPrice()));
                                    lineItemEntity.setAmount(parseDoubleSafe(itemInfo.getAmount()));
                                    lineItemEntity.setAssessableValue(parseDoubleSafe(itemInfo.getAssessableValue()));
                                    lineItemEntity.setIgstAssessableValue(parseDoubleSafe(itemInfo.getIgstAssessableValue()));
                                    
                                    // Store notifications as JSON
                                    if (lineItem.getNotifications() != null) {
                                        try {
                                            String notificationsJson = objectMapper.writeValueAsString(lineItem.getNotifications());
                                            lineItemEntity.setNotificationsJson(notificationsJson);
                                        } catch (Exception e) {
                                            LOGGER.warn("Failed to serialize notifications JSON: {}", e.getMessage());
                                        }
                                    }
                                    
                                    // Store license as JSON
                                    if (lineItem.getLicense() != null && !lineItem.getLicense().isEmpty()) {
                                        try {
                                            String licenseJson = objectMapper.writeValueAsString(lineItem.getLicense());
                                            lineItemEntity.setLicenseJson(licenseJson);
                                        } catch (Exception e) {
                                            LOGGER.warn("Failed to serialize license JSON: {}", e.getMessage());
                                        }
                                    }
                                    
                                    lineItemRepo.save(lineItemEntity);
                                    LOGGER.info("✅ Saved line item: {}", itemInfo.getPartCode());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save invoices/line items: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save invoices and line items", e);
        }
    }

    private void saveBonds(ImportJobRequestDTO requestDTO, Long jobId) {
        try {
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails().getBond() != null) {
                
                for (ImportJobRequestDTO.Bond bondDTO : requestDTO.getJob().getShipmentDetails().getJobDetails().getBond()) {
                    UTImportJobBond bond = new UTImportJobBond();
                    bond.setJobId(jobId);
                    bond.setBondNo(safeSubstring(bondDTO.getBondNo(), 50));
                    bond.setBondCode(safeSubstring(bondDTO.getCode(), 50));
                    bond.setBondPort(safeSubstring(bondDTO.getPort(), 100));
                    
                    bondRepo.save(bond);
                    LOGGER.info("✅ Saved bond: {}", bondDTO.getBondNo());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save bonds: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save bonds", e);
        }
    }

    private void saveCertificates(ImportJobRequestDTO requestDTO, Long jobId) {
        try {
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails().getCertificate() != null) {
                
                for (ImportJobRequestDTO.Certificate certDTO : requestDTO.getJob().getShipmentDetails().getJobDetails().getCertificate()) {
                    UTImportJobCertificate certificate = new UTImportJobCertificate();
                    certificate.setJobId(jobId);
                    certificate.setCertificateNo(safeSubstring(certDTO.getCertificateNo(), 50));
                    certificate.setCertificateType(safeSubstring(certDTO.getType(), 50));
                    certificate.setCertificateDate(parseDateSafe(certDTO.getDate()));
//                    certificate.setCommissionerate(safeSubstring(certDTO.getCommissionerate(), 200));
                    certificate.setRangeName(safeSubstring(certDTO.getRange(), 100));
                    certificate.setDivision(safeSubstring(certDTO.getDivision(), 100));
                    
                    certificateRepo.save(certificate);
                    LOGGER.info("✅ Saved certificate: {}", certDTO.getCertificateNo());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save certificates: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save certificates", e);
        }
    }

    private void saveHssList(ImportJobRequestDTO requestDTO, Long jobId) {
        try {
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails().getHss() != null) {
                
                for (ImportJobRequestDTO.Hss hssDTO : requestDTO.getJob().getShipmentDetails().getJobDetails().getHss()) {
                    UTImportJobHss hss = new UTImportJobHss();
                    hss.setJobId(jobId);
                    hss.setName(safeSubstring(hssDTO.getName(), 200));
                    hss.setPartyId(safeSubstring(hssDTO.getPartyId(), 50));
                    hss.setIec(safeSubstring(hssDTO.getIec(), 50));
                    hss.setBranch(safeSubstring(hssDTO.getBranch(), 10));
                    hss.setAddress1(safeSubstring(hssDTO.getAddress1(), 500));
                    hss.setAddress2(safeSubstring(hssDTO.getAddress2(), 500));
                    hss.setCity(safeSubstring(hssDTO.getCity(), 100));
                    hss.setState(safeSubstring(hssDTO.getState(), 100));
                    hss.setPincode(safeSubstring(hssDTO.getPincode(), 20));
                    hss.setCountry(safeSubstring(hssDTO.getCountry(), 50));
                    hss.setAdCode(safeSubstring(hssDTO.getAdCode(), 100));
                    hss.setPrecedingLevel(safeSubstring(hssDTO.getPrecedingLevel(), 10));
                    
                    hssRepo.save(hss);
                    LOGGER.info("✅ Saved HSS record: {}", hssDTO.getName());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save HSS list: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save HSS list", e);
        }
    }

    private void saveStatements(ImportJobRequestDTO requestDTO, Long jobId) {
        try {
            // Save job level statements
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails().getStatements() != null) {
                
                for (ImportJobRequestDTO.Statement statement : requestDTO.getJob().getShipmentDetails().getJobDetails().getStatements()) {
                    if (statement.getStatementInfo() != null) {
                        UTImportJobStatement stmt = new UTImportJobStatement();
                        stmt.setJobId(jobId);
                        stmt.setStatementLevel("jobLevel");
                        stmt.setStatementType(statement.getStatementInfo().getType());
                        stmt.setStatementCode(statement.getStatementInfo().getCode());
                        stmt.setStatementText(safeSubstring(statement.getStatementInfo().getText(), 2000));
                        
                        // FIX: Handle isMandatory as String, not boolean
                        if (statement.getIsMandatory() != null) {
                            String isMandatory = statement.getIsMandatory().toString();
                            if ("true".equalsIgnoreCase(isMandatory) || "Y".equalsIgnoreCase(isMandatory)) {
                                stmt.setIsMandatory("Y");
                            } else {
                                stmt.setIsMandatory("N");
                            }
                        } else {
                            stmt.setIsMandatory("N");
                        }
                        
                        statementRepo.save(stmt);
                        LOGGER.info("✅ Saved job level statement: {}", statement.getStatementInfo().getCode());
                    }
                }
            }
            
            // Save invoice level statements
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getInvoiceDetails() != null) {
                
                for (ImportJobRequestDTO.InvoiceDetail invoiceDetail : requestDTO.getJob().getShipmentDetails().getInvoiceDetails()) {
                    if (invoiceDetail.getStatement() != null) {
                        for (ImportJobRequestDTO.Statement statement : invoiceDetail.getStatement()) {
                            if (statement.getStatementInfo() != null) {
                                UTImportJobStatement stmt = new UTImportJobStatement();
                                stmt.setJobId(jobId);
                                stmt.setStatementLevel("invoiceLevel");
                                stmt.setStatementType(statement.getStatementInfo().getType());
                                stmt.setStatementCode(statement.getStatementInfo().getCode());
                                stmt.setStatementText(safeSubstring(statement.getStatementInfo().getText(), 2000));
                                
                                // FIX: Handle isMandatory as String, not boolean
                                if (statement.getIsMandatory() != null) {
                                    String isMandatory = statement.getIsMandatory().toString();
                                    if ("true".equalsIgnoreCase(isMandatory) || "Y".equalsIgnoreCase(isMandatory)) {
                                        stmt.setIsMandatory("Y");
                                    } else {
                                        stmt.setIsMandatory("N");
                                    }
                                } else {
                                    stmt.setIsMandatory("N");
                                }
                                
                                statementRepo.save(stmt);
                                LOGGER.info("✅ Saved invoice level statement: {}", statement.getStatementInfo().getCode());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save statements: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save statements", e);
        }
    }
    private void saveSupportingDocs(ImportJobRequestDTO requestDTO, Long jobId) {
        try {
            // Save job level supporting docs
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getJobDetails().getSupportingDocs() != null) {
                
                for (ImportJobRequestDTO.SupportingDoc doc : requestDTO.getJob().getShipmentDetails().getJobDetails().getSupportingDocs()) {
                    UTImportJobSupportingDoc supportingDoc = new UTImportJobSupportingDoc();
                    supportingDoc.setJobId(jobId);
                    supportingDoc.setDocLevel("jobLevel");
                    supportingDoc.setDocType(safeSubstring(doc.getDocType(), 100));
//                    supportingDoc.setDocInfo(safeSubstring(doc.getDocInfo(), 1000));
                    
                    supportingDocRepo.save(supportingDoc);
                    LOGGER.info("✅ Saved job level supporting doc");
                }
            }
            
            // Save invoice level supporting docs
            if (requestDTO.getJob().getShipmentDetails() != null &&
                requestDTO.getJob().getShipmentDetails().getInvoiceDetails() != null) {
                
                for (ImportJobRequestDTO.InvoiceDetail invoiceDetail : requestDTO.getJob().getShipmentDetails().getInvoiceDetails()) {
                    if (invoiceDetail.getSupportingDocs() != null) {
                        for (ImportJobRequestDTO.SupportingDoc doc : invoiceDetail.getSupportingDocs()) {
                            UTImportJobSupportingDoc supportingDoc = new UTImportJobSupportingDoc();
                            supportingDoc.setJobId(jobId);
                            supportingDoc.setDocLevel("invoiceLevel");
                            supportingDoc.setDocType(safeSubstring(doc.getDocType(), 100));
//                            supportingDoc.setDocInfo(safeSubstring(doc.getDocInfo(), 1000));
                            
                            supportingDocRepo.save(supportingDoc);
                            LOGGER.info("✅ Saved invoice level supporting doc");
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save supporting docs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save supporting docs", e);
        }
    }

    private String safeSubstring(String value, int maxLength) {
        if (value == null) return null;
        if (value.length() <= maxLength) return value;
        LOGGER.warn("Truncating value from {} to {} characters", value.length(), maxLength);
        return value.substring(0, maxLength);
    }

    private java.time.LocalDateTime parseDateSafe(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            if (dateString.endsWith("Z")) {
                return java.time.LocalDateTime.parse(dateString, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
            } else if (dateString.contains("T")) {
                return java.time.LocalDateTime.parse(dateString, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else {
                return java.time.LocalDate.parse(dateString).atStartOfDay();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse date: {}, Error: {}", dateString, e.getMessage());
            return null;
        }
    }

    private Double parseDoubleSafe(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        try {
            String cleanValue = value.replace(",", "").trim();
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            LOGGER.warn("Failed to parse double: {}, Error: {}", value, e.getMessage());
            return 0.0;
        }
    }

    @Override
    @Transactional
    public List<UTImportJob> getAllImportJobs() {
        LOGGER.info("Fetching all import jobs");
        return importJobRepo.findAll();
    }

    @Override
    @Transactional
    public UTImportJob getImportJobById(Long jobId) {
        LOGGER.info("Fetching import job by ID: {}", jobId);
        return importJobRepo.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Import Job not found with ID: " + jobId));
    }

    @Override
    @Transactional
    public UTImportJob getImportJobByReferenceId(String referenceId) {
        LOGGER.info("Fetching import job by Reference ID: {}", referenceId);
        return importJobRepo.findByReferenceId(referenceId)
                .orElseThrow(() -> new EntityNotFoundException("Import Job not found with Reference ID: " + referenceId));
    }
}