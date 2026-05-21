package com.invoice.approval.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.Instant;

@Data  // This generates all getters, setters, equals, hashCode, toString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ut_exportjob")
public class UTExportJob {
    
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "export_job_seq_gen")
    @SequenceGenerator(name = "export_job_seq_gen", sequenceName = "SEQ_UT_EXPORT_JOB", allocationSize = 1)
    @Column(name = "job_id")
    private Long jobId;
    
    @Column(name = "reference_id", length = 100)
    private String referenceId;
    
    @Column(name = "organization_id", length = 100)
    private String organizationId;
    
    @Column(name = "organization_branch_code", length = 50)
    private String organizationBranchCode;
    
    @Column(name = "organization_name", length = 200)
    private String organizationName;
    
    @Column(name = "job_type", length = 50)
    private String jobType;
    
    @Column(name = "time_stamp")
    private Instant timeStamp;
    
    @Column(name = "tenant_name", length = 200)
    private String tenantName;
    
    @Column(name = "tenant_branch", length = 50)
    private String tenantBranch;
    
    @Column(name = "job_no", length = 100)
    private String jobNo;
    
    @Column(name = "job_info_job_type", length = 50)
    private String jobInfoJobType;
    
    @Column(name = "mot", length = 50)
    private String mot;
    
    @Column(name = "customs_house_code", length = 100)
    private String customsHouseCode;
    
    @Column(name = "customer_ref_no", length = 200)
    private String customerRefNo;
    
    @Column(name = "exporter_name", length = 200)
    private String exporterName;
    
    @Column(name = "exporter_party_id", length = 100)
    private String exporterPartyId;
    
    @Column(name = "exporter_pan_no", length = 50)
    private String exporterPanNo;
    
    @Column(name = "exporter_address1", length = 500)
    private String exporterAddress1;
    
    @Column(name = "exporter_address2", length = 500)
    private String exporterAddress2;
    
    @Column(name = "exporter_city", length = 100)
    private String exporterCity;
    
    @Column(name = "exporter_pincode", length = 20)
    private String exporterPincode;
    
    @Column(name = "exporter_state", length = 100)
    private String exporterState;
    
    @Column(name = "exporter_country", length = 100)
    private String exporterCountry;
    
    @Column(name = "consignee_name", length = 200)
    private String consigneeName;
    
    @Column(name = "consignee_branch", length = 50)
    private String consigneeBranch;
    
    @Column(name = "consignee_address1", length = 500)
    private String consigneeAddress1;
    
    @Column(name = "consignee_address2", length = 500)
    private String consigneeAddress2;
    
    @Column(name = "consignee_city", length = 100)
    private String consigneeCity;
    
    @Column(name = "consignee_pincode", length = 20)
    private String consigneePincode;
    
    @Column(name = "consignee_state", length = 100)
    private String consigneeState;
    
    @Column(name = "consignee_country", length = 100)
    private String consigneeCountry;
    
    @Column(name = "port_of_loading", length = 100)
    private String portOfLoading;
    
    @Column(name = "port_of_discharge", length = 100)
    private String portOfDischarge;
    
    @Column(name = "country_of_discharge", length = 100)
    private String countryOfDischarge;
    
    @Column(name = "port_of_final_destination", length = 100)
    private String portOfFinalDestination;
    
    @Column(name = "country_of_final_destination", length = 100)
    private String countryOfFinalDestination;
    
    @Column(name = "hawb_hbl_no", length = 100)
    private String hawbHblNo;
    
    @Column(name = "gross_weight")
    private Double grossWeight;
    
    @Column(name = "net_weight")
    private Double netWeight;
    
    @Column(name = "weight_uom", length = 20)
    private String weightUom;
    
    @Column(name = "chargeable_weight")
    private Double chargeableWeight;
    
    @Column(name = "no_of_packages")
    private Integer noOfPackages;
    
    @Column(name = "package_code", length = 50)
    private String packageCode;
    
    @Column(name = "no_of_containers")
    private Integer noOfContainers;
    
    @Column(name = "invoice_no", length = 100)
    private String invoiceNo;
    
    @Column(name = "invoice_date")
    private Instant invoiceDate;
    
    @Column(name = "invoice_value")
    private Double invoiceValue;
    
    @Column(name = "invoice_currency", length = 10)
    private String invoiceCurrency;
    
    @Column(name = "nature_of_contract", length = 100)
    private String natureOfContract;
    
    @Column(name = "invoice_total_gross_weight", length = 50)
    private String invoiceTotalGrossWeight;
    
    @Column(name = "invoice_total_net_weight", length = 50)
    private String invoiceTotalNetWeight;
    
    @Column(name = "invoice_weight_uom", length = 20)
    private String invoiceWeightUom;
    
    @Column(name = "invoice_no_of_packages", length = 50)
    private String invoiceNoOfPackages;
    
    @Column(name = "invoice_package_code", length = 50)
    private String invoicePackageCode;
    
    @Column(name = "invoice_buyer_name", length = 200)
    private String invoiceBuyerName;
    
    @Column(name = "invoice_buyer_address1", length = 500)
    private String invoiceBuyerAddress1;
    
    @Column(name = "invoice_buyer_city", length = 100)
    private String invoiceBuyerCity;
    
    @Column(name = "invoice_exporter_name", length = 200)
    private String invoiceExporterName;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "created_on", length = 50)
    private String createdOn;
    
    @Column(name = "modified_by", length = 100)
    private String modifiedBy;
    
    @Column(name = "modified_on", length = 50)
    private String modifiedOn;
}