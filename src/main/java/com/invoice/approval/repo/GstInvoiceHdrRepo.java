package com.invoice.approval.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.invoice.approval.entity.GstInvoiceHdrVO;

public interface GstInvoiceHdrRepo extends JpaRepository<GstInvoiceHdrVO, Long> {

	
	@Query(nativeQuery = true,value = "select gst_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,"
			+ "b.creditdays,b.creditlimit,approve1,approve2,approve3,approve1name,approve1on from gst_invoicehdr a,mg_partyhdr b \r\n"
			+ "where invproceed = 'F' and approve1 = 'F' and a.partycode = b.party_code and approve1name is null"
			+ " AND a.branchcode in (select branchcode from vg_userbranch where userName =?1)  order by a.createdon desc")
	Set<Object[]> getPendingDetailsApprove1(String userName);

	@Query(value = "select a from GstInvoiceHdrVO a where a.gstInvoiceHdrId=?1")
	GstInvoiceHdrVO findByGstInvoiceHdrId(Long id);
	
	@Query(nativeQuery = true,value = "select gst_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,b.creditdays,b.creditlimit,approve1,approve1name,approve1on,approve2,approve2name,approve2on,approve3,approve3name,approve3on \r\n"
			+ "from gst_invoicehdr a,mg_partyhdr b \r\n"
			+ "where invproceed = 'F' and approve1 = 'T' and a.partycode = b.party_code and  approve2='F' and approve2name is null and a.branchcode in (select branchcode from vg_userbranch where userName =?1) order by a.createdon desc")
	Set<Object[]> getPendingDetailsApprove2(String userName);
	
	
	@Query(nativeQuery = true,value = "select gst_invoicehdrid,branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,creditdays,creditlimit,approve1,approve2,approve3,approve1name,approve1on,approve2name,approve2on from gst_invoicehdr \r\n"
			+ "where invproceed = 'F' and approve1 = 'T' and approve1name is not null "
			 + " AND branchcode in (select branchcode from vg_userbranch where userName =?1) order by createdon desc")
	Set<Object[]> getInvDetailsApprove1(String userName);
	
	@Query(nativeQuery = true,value = "select gst_invoicehdrid,branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,creditdays,creditlimit,approve1,approve2,approve3,approve1name,approve1on,approve2name,approve2on from gst_invoicehdr\r\n"
			+ "where invproceed = 'F' and approve2 = 'T' and approve2name is not null\r\n"
			+ "AND branchcode in (select branchcode from vg_userbranch where userName =?1)  order by createdon desc")
	Set<Object[]> getInvDetailsApprove2(String userName);
	
	

}
