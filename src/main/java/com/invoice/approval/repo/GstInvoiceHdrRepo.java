package com.invoice.approval.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.invoice.approval.entity.GstInvoiceHdrVO;

public interface GstInvoiceHdrRepo extends JpaRepository<GstInvoiceHdrVO, Long> {

	
	@Query(nativeQuery = true,value = "select gst_invoicehdrid,branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,creditdays,creditlimit,approve1,approve2,approve3,approve1name,approve1on from gst_invoicehdr \r\n"
			+ "where invproceed = 'F' and approve1 = 'F' and approve1name is null order by createdon desc")
	Set<Object[]> getPendingDetailsApprove1();

	@Query(value = "select a from GstInvoiceHdrVO a where a.gstInvoiceHdrId=?1")
	GstInvoiceHdrVO findByGstInvoiceHdrId(Long id);
	
	@Query(nativeQuery = true,value = "select gst_invoicehdrid,branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,creditdays,creditlimit,approve1,approve1name,approve1on,approve2,approve2name,approve2on,approve3,approve3name,approve3on \r\n"
			+ "from gst_invoicehdr \r\n"
			+ "where invproceed = 'F' and approve1 = 'T' and approve2='F' and approve2name is null order by createdon desc")
	Set<Object[]> getPendingDetailsApprove2();

}
