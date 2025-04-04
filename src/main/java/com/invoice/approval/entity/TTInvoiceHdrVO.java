
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
	@Table(name = "TT_INVOICEHDR")
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	

	public class TTInvoiceHdrVO {
		
		
		@Id
	    @Column(name = "TT_INVOICEHDRID")
	    private Long TTInvoiceHdrId;
		

		@Column(name = "approve1", length = 1)
	    private String approve1;

	    @Column(name = "APPROVE1NAME", length = 30)
	    private String approve1Name;

	    @Column(name = "APPROVE1ON")
	    private LocalDateTime approve1On;
	    

	    
	    @Column(name = "approveemail", length = 1)
	    private String approveEmail;

	    
	    
	    @Column(name = "eligislab")
	    private int eligiSlab;
	    
	    @Column(name = "slabremarks", length = 30)
	    private String slabRemarks;
	    
	    @Column(name = "exceeddays", length = 1)
	    private String exceedDays;
	    
	    @Column(name = "invproceed", length = 1)
	    private String invProceed;
	    
	    
	


}
