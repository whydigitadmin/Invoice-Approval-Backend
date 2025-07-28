

package com.invoice.approval.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.invoice.approval.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "GST_POVENDOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorVO {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "povendorgen")
	@SequenceGenerator(name = "povendorappgen", sequenceName = "povendorappseq", initialValue = 1000000001, allocationSize = 1)
    @Column(name = "GST_POVENDORID")
    private Long id;
	
	@Column(name = "cancel", length = 1)
    private String cancel="F";
	
	
	@Column(name = "vendorname", length = 100)
    private String vendorname;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "gstin", length = 20)
    private String gstin;
    
    @Column(name = "contactperson", length = 50)
    private String contactperson;

    @Column(name = "contactno", length = 10)
    private String contactno;
    
    @Column(name = "panno", length = 10)
    private String panno;
    
    @Column(name = "emailid", length = 100)
    private String emailid;

    @Column(name = "createdby", length = 30)
    private String createdBy;

    @Column(name = "modifiedby", length = 30)
    private String modifiedBy;

    
	@Embedded
	private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();
}






