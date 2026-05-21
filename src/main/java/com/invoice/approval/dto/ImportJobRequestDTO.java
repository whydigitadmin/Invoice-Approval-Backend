package com.invoice.approval.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobRequestDTO {
    private Job job;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Job {
        private HeaderDetails headerDetails;
        private ShipmentDetailsWrapper shipmentDetails;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeaderDetails {
        private String timeStamp;
        private String organizationId;
        private String organizationBranchCode;
        private String organizationName;
        private String jobNo;
        private String jobDate;
        private String jobType;
        private String referenceId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipmentDetailsWrapper {
        private JobDetails jobDetails;
        private List<InvoiceDetail> invoiceDetails;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobDetails {
        private String jobStatus;
        private Tenants tenants;
        private Importer importer;
        private JobInfo jobInfo;
        private CommercialTax commercialTax;
        private Debug debug;
        private List<ExchangeRate> exchangeRates;
        private Supplier supplier;
        private List<Bond> bond;
        private List<Certificate> certificate;
        private List<Hss> hss;
        private Warehouse warehouse;
        private Reports reports;
        private List<IgmDetail> igmDetails;
        private List<Statement> statements;
        private List<SupportingDoc> supportingDocs;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tenants {
    	private Cb cb;
        private Agent agent;
        private Dsp dsp;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cb {
        private String name;
        private String branch;
        private String licenseNo;
        private String addressLine;
        private String city;
        private String pincode;
        private String state;
        private String country;
        private String icegateId;
        private String submittedBy;
        private String aeoRegNo;
        private String aeoRole;
        private String transactionNo;
        private List<String> assignedTo;
        private String customerBranch;
        private String ownerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Agent {
        private List<String> assignedTo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dsp {
        private List<String> assignedTo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tenant {
        private String name;
        private String branch;
        private String licenseNo;
        private String addressLine;
        private String city;
        private String pincode;
        private String state;
        private String country;
        private String icegateId;
        private String submittedBy;
        private String aeoRegNo;
        private String aeoRole;
        private String transactionNo;
        private List<String> assignedTo;
        private String customerBranch;
        private String ownerId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Importer {
        private String name;
        private String partyId;
        private String iec;
        private String branch;
        private String panNo;
        private String address1;
        private String address2;
        private String city;
        private String pincode;
        private String state;
        private String country;
        private String type;
        private String adCode;
        private String icegateId;
        private String aeoStatusCategory;
        private String bankName;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobInfo {
        private String jobType;
        private String jobNo;
        private String beType;
        private String customsHouseCode;
        private String mot;
        private String priorBe;
        private String kachchaBe;
        private String paymentMethodCode;
        private String greenChannel;
        private String section48;
        private String firstCheck;
        private String provisionalAssessment;
        private String hssTransaction;
        private String controlNo;
        private String flatfileSeqNo;
        private List<String> includedSections;
        private String additionalChargesHss;
        private String beGrossTotal;
        private String countryOfConsignment;
        private String countryOfOrigin;
        private String customerRefNo;
        private String firstCheckReason;
        private String igstTotalAssessableValue;
        private String miscLoad;
        private String miscLoadAmt;
        private String portOfOrigin;
        private String portOfShipment;
        private String provisionalAssessmentReason;
        private String remarks;
        private String section48Reason;
        private String totalAssessableValue;
        private String totalDuty;
        private String ucrNo;
        private String ucrType;
        private String zBeType;
        private String beDate;
        private String beNo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommercialTax {
        private String regNo;
        private String type;
        private String state;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Debug {
        private Boolean statusCall;
        private IsConditionApplied isConditionApplied;
        private String zohoContactId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IsConditionApplied {
        private Boolean isReimport;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExchangeRate {
        private String currencyCode;
        private Integer currencyCount;
        private String standardCurrency;
        private String effectiveDate;
        private Integer unitInRs;
        private String rate;
        private String certificateNo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Supplier {
        private String address1;
        private String address2;
        private String address3;
        private String branch;
        private String country;
        private String name;
        private String partyId;
        private String city;
        private String pincode;
        private String state;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bond {
        private String bondNo;
        private String code;
        private String port;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Certificate {
        private String certificateNo;
        private String type;
        private String date;
        private String range;
        private String division;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hss {
        private String name;
        private String partyId;
        private String iec;
        private String branch;
        private String address1;
        private String address2;
        private String city;
        private String state;
        private String pincode;
        private String country;
        private String adCode;
        private String precedingLevel;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Warehouse {
        private String beDate;
        private String beNo;
        private String code;
        private String customsSiteId;
        private String grossWeight;
        private String jobNo;
        private String packageCode;
        private String packagesReleased;
        private String uom;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reports {
        private String freightValueInInr;
        private String insuranceValueInInr;
        private String invoiceValueInInr;
        private String addlExciseTotalDuty;
        private String bcdTotalDuty;
        private String customsHealthCessTotalDuty;
        private String swsTotalDuty;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IgmDetail {
        private Igm igm;
        private List<Container> containers;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Igm {
        private String marksAndNos1;
        private String cargoType;
        private String chargeableWeight;
        private String gatewayDate;
        private String gatewayNo;
        private String gatewayPort;
        private String grossWeight;
        private String hawbHblDate;
        private String hawbHblNo;
        private String igmDate;
        private String igmNo;
        private String inwardDate;
        private String marksAndNos2;
        private String marksAndNos3;
        private String mawbMblDate;
        private String mawbMblNo;
        private String packageCode;
        private String totalNoOfPackages;
        private String uom;
        private String eta;
        private String bkVesselFlt;
        private String bkVoyage;
        private String carrierCode;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Container {
        private String containerNo;
        private String sealNo;
        private String lclFcl;
        private String type;
        private String truckNo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statement {
        private StatementInfo statementInfo;
        private String tableLevel;
        private Boolean isMandatory;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatementInfo {
        private String type;
        private String code;
        private String text;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupportingDoc {
        private String docType;
        private String docFileId;
    }
    
    // ==================== INVOICE RELATED DTOs ====================
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceDetail {
        private InvoiceInfo invoiceInfo;
        private Svb svb;
        private Buyer buyer;
        private Charges charges;
        private String docFileId;
        private Supplier supplier;
        private Broker broker;
        private OldValueAsPerInvoice oldValueAsPerInvoice;
        private Seller seller;
        private ThirdParty thirdParty;
        private Debug debug;
        private Reports reports;
        private SubCharges subCharges;
        private List<LineItem> lineItems;
        private List<Statement> statement;
        private List<SupportingDoc> supportingDocs;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceInfo {
        private String natureOfTransaction;
        private String valuationMethod;
        private String paymentTerms;
        private String invoiceCurrency;
        private String invoiceDate;
        private String invoiceNo;
        private String invoiceValue;
        private String termsOfInvoice;
        private String contractDate;
        private String contractNo;
        private String lcDate;
        private String lcNo;
        private String natureOfDiscount;
        private String poDate;
        private String poNo;
        private String salesCondition1;
        private String salesCondition2;
        private String salesCondition3;
        private String salesCondition4;
        private String salesCondition5;
        private String totalAssessableValue;
        private String totalDuty;
        private String otherRelatedInfo;
        private String termsPlace;
        private String totalItemValue;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Svb {
        private String isRelated;
        private String customsHouseCode;
        private String date;
        private String flag;
        private String loadOnAssessableValue;
        private String loadOnDuty;
        private String refNo;
        private String whetherLoadOnAssessable;
        private String whetherLoadOnDuty;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Buyer {
        private String address1;
        private String name;
        private String address2;
        private String partyId;
        private String pincode;
        private String branch;
        private String city;
        private String country;
        private String icegateId;
        private String state;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Charges {
        private Freight freight;
        private Insurance insurance;
        private AgencyCommission agencyCommission;
        private Discount discount;
        private HssCharge hss;
        private Loading loading;
        private Misc misc;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Freight {
        private String rate;
        private String isActual;
        private String amount;
        private String currency;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Insurance {
        private String rate;
        private String amount;
        private String currency;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgencyCommission {
        private String amount;
        private String currency;
        private String rate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {
        private String amount;
        private String rate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HssCharge {
        private String amount;
        private String rate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Loading {
        private String amount;
        private String currency;
        private String rate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Misc {
        private String amount;
        private String currency;
        private String rate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Broker {
        private String address1;
        private String address2;
        private String address3;
        private String country;
        private String name;
        private String partyId;
        private String pincode;
        private String city;
        private String state;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OldValueAsPerInvoice {
        private String invoiceValue;
        private String invoiceCurrency;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Seller {
        private String address1;
        private String address2;
        private String country;
        private String name;
        private String partyId;
        private String pincode;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThirdParty {
        private String address1;
        private String address2;
        private String aeoCode;
        private String aeoCountry;
        private String aeoRole;
        private String city;
        private String country;
        private String name;
        private String partyId;
        private String pincode;
        private String subDivision;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubCharges {
        private ChargeItem brokerageAndCommission;
        private ChargeItem costOfContainers;
        private ChargeItem costOfGoodsAndServices;
        private ChargeItem costOfPacking;
        private ChargeItem costOfWarrantyServices;
        private ChargeItem countryOfOriginCertificate;
        private ChargeItem documentation;
        private ChargeItem handlingCharges;
        private ChargeItem loadingCharges;
        private ChargeItem otherChargesAndPayments;
        private ChargeItem otherCostOrPayments;
        private ChargeItem royaltiesAndLicenseFees;
        private ChargeItem unloadingCharges;
        private ChargeItem valueOfProceedsWhichAccrue;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeItem {
        private String amount;
        private String rate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem {
        private LineItemInfo lineItemInfo;
        private Manufacturer manufacturer;
        private Notifications notifications;
        private Rsp rsp;
        private OldValuesAsPerItem oldValuesAsPerItem;
        private Reports reports;
        private List<Object> sezBE;
        private Object svb;
        private List<Object> swInfo;
        private Object fta;
        private List<Object> license;
        private PreviousBE previousBE;
        private List<Object> reImport;
        private List<Object> section65;
        private List<Object> swConstituent;
        private List<Object> swControl;
        private List<Object> swProduction;
        private List<Object> statement;
        private List<Object> supportingDocs;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItemInfo {
        private String accessoryStatus;
        private String amount;
        private String brand;
        private String ceth;
        private String cusUom;
        private String custNtfnExmpCentralExcFlag;
        private String customsDescription;
        private String foc;
        private String genericDescription;
        private String hsn;
        private String invoiceDescription;
        private String isReimport;
        private String isSection65;
        private String model;
        private String partCode;
        private String preferentialOrStandard;
        private String qty;
        private String specificUom2;
        private String unitPrice;
        private String cusQty;
        private String assessableValue;
        private String totalDuty;
        private List<String> concatArray;
        private String accessories;
        private String batchNo;
        private String countryOfOrigin;
        private String endUseCode;
        private String igstAssessableValue;
        private String sourceCountry;
        private String specificQty1;
        private String specificQty2;
        private String specificUom1;
        private String transitCountry;
        private String uom;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Manufacturer {
        private String name;
        private String isSameAsSupplier;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String code;
        private String codeType;
        private String country;
        private String pincode;
        private String subDivision;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Notifications {
        private Duty bcd;
        private Duty caidc;
        private Duty socialWelfare;
        private Duty igstLevy;
        private Duty igstCompCessLevy;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Duty {
        private String ntfnNo;
        private String ntfnSlNo;
        private String dutyAmount;
        private String rate;
        private String flag;
        private String specAmount;
        private String specUqc;
        private String standardValue;
        private String notificationValue;
        private StandardDetails standardDetails;
        private NotificationDetails notificationDetails;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StandardDetails {
        private Integer RTA;
        private Object AMTS;
        private String UQC;
        private String CTH;
        private String FLG;
        private String PFLG;
        private String PRTA;
        private String PAMTS;
        private String PUQC;
        private String BCD_AMTS3;
        private String BCD_UQC3;
        private String updatedDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationDetails {
        private String NOTN;
        private String SLNO;
        private String CTH;
        private String UQC;
        private String AD_FLG;
        private String updatedDate;
        private String RTA;
        private String AMTS;
        private String FLG;
        private String NOTN_TYPE;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rsp {
        private String isRsp;
        private String description;
        private String itemSlNoInRSP;
        private String ntfnNo;
        private String ntfnSlNo;
        private String qty;
        private String totalSaleAmount;
        private String unit;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OldValuesAsPerItem {
        private String amount;
        private String qty;
        private String unitPrice;
        private String cusQty;
        private String currency;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreviousBE {
        private String beNo;
        private String currency;
        private String customsHouseCode;
        private String date;
        private String unitPrice;
    }
}