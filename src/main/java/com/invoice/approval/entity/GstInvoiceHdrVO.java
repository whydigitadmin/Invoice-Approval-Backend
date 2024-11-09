package com.invoice.approval.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GST_INVOICEHDR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GstInvoiceHdrVO {
	
	@Id
    @Column(name = "GST_INVOICEHDRID")
    private Long gstInvoiceHdrId;
	

	@Column(name = "approve1", length = 1)
    private String approve1;

    @Column(name = "APPROVE1NAME", length = 30)
    private String approve1Name;

    @Column(name = "APPROVE1ON")
    private LocalDateTime approve1On;
    
    @Column(name = "approve2", length = 1)
    private String approve2;

    @Column(name = "APPROVE2NAME", length = 30)
    private String approve2Name;

    @Column(name = "APPROVE2ON")
    private LocalDateTime approve2On;
    
    @Column(name = "approve3", length = 1)
    private String approve3;

    @Column(name = "APPROVE3NAME", length = 30)
    private String approve3Name;

    @Column(name = "APPROVE3ON")
    private LocalDateTime approve3On;

}
