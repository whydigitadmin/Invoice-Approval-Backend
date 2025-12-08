package com.invoice.approval.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.invoice.approval.entity.TTInvoiceHdrVO;

public interface TTInvoiceHdrRepo extends JpaRepository<TTInvoiceHdrVO, Long> {

	@Query(nativeQuery = true,value = "select tt_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,"
			+ "b.creditdays,b.creditlimit,slabremarks,exceeddays,eligislab,unapproveamt,approve1,null approve2,null approve3,approve1name,approve1on,osbeyond,excesscredit,category,controllingoffice , case when lower(salesperson) like 'uwl%' then 'Mr./Ms. '||initcap(salespersonname) else initcap(salespersonname) end salespersonname from tt_invoicehdr a,mg_partyhdr b \r\n"
			+ "where invproceed = 'F' and approve1 = 'F' and a.partycode = b.party_code and approve1name is null"
			+ " AND eligislab = 1 AND a.branchcode in (select branchcode from vg_userbranch where (userName =?1 or 'admin'=?1 ))  order by a.createdon desc")
	Set<Object[]> getPendingDetailsApprove1slab1(String userName);
	
	

	@Query(value = "select a from TTInvoiceHdrVO a where a.TTInvoiceHdrId=?1")
	TTInvoiceHdrVO findByTTInvoiceHdrId(Long id);
	
	@Query(nativeQuery = true,value = "select tt_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,b.creditdays,b.creditlimit,slabremarks,decode(exceeddays,'91','90P',exceeddays)exceeddays,eligislab,unapproveamt,approve1,approve1name,approve1on,null approve2,null approve2name,null approve2on,null approve3,null approve3name,null approve3on,osbeyond,excesscredit,category,controllingoffice, case when lower(salesperson) like 'uwl%' then 'Mr./Ms. '||initcap(salespersonname) else initcap(salespersonname) end salespersonname \r\n"
			+ "from tt_invoicehdr a,mg_partyhdr b \r\n"
			+ "where  eligislab = 1 and approve1 = 'T' and approve1name is not null and a.partycode = b.party_code and a.branchcode in (select branchcode from vg_userbranch where (userName =?1 or 'admin'=?1 )) order by a.createdon desc")
	Set<Object[]> getPendingDetailsApprove2slab1(String userName);
	
	@Query(nativeQuery = true,value = "select gst_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,b.creditdays,b.creditlimit,slabremarks,decode(exceeddays,'91','90P',exceeddays)exceeddays,eligislab,unapproveamt,approve1,approve1name,approve1on,null approve2,null approve2name,null approve2on,null approve3,null approve3name,null approve3on,osbeyond,excesscredit,category,controllingoffice,case when lower(salesperson) like 'uwl%' then 'Mr./Ms. '||initcap(salespersonname) else initcap(salespersonname) end salespersonname \r\n"
			+ "from gst_invoicehdr a,mg_partyhdr b \r\n"
			+ "where invproceed = 'F' and eligislab in (2,3) and approve1 = 'F' and a.partycode = b.party_code and approve1name is null and a.branchcode in (select branchcode from vg_userbranch where userName =?1) order by a.createdon desc")
	Set<Object[]> getPendingDetailsApprove1slab2(String userName);
	
	@Query(nativeQuery = true,value = "select tt_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,"
			+ "b.creditdays,b.creditlimit,slabremarks,exceeddays,eligislab,unapproveamt,approve1,null approve2,null approve3,approve1name,approve1on,osbeyond,excesscredit,category,controllingoffice , case when lower(salesperson) like 'uwl%' then 'Mr./Ms. '||initcap(salespersonname) else initcap(salespersonname) end salespersonname from tt_invoicehdr a,mg_partyhdr b \r\n"
			+ "where invproceed = 'F' and approve1 = 'F' and a.partycode = b.party_code and approve1name is null"
			+ " AND eligislab in (1,2,3) AND a.branchcode in (select branchcode from vg_userbranch where (userName =?1 or 'admin'=?1 ))  order by a.createdon desc")
	Set<Object[]> getAdminPendingDetailsApprove1slab1(String userName);
	
	
	
	@Query(nativeQuery = true,value = "select tt_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,b.creditdays,b.creditlimit,unapproveamt,approve1,null approve2,null approve3,approve1name,approve1on,null approve2name,null approve2on,null approve3on,osbeyond,excesscredit,category,controllingoffice,case when lower(salesperson) like 'uwl%' then 'Mr./Ms. '||initcap(salespersonname) else initcap(salespersonname) end salespersonname,slabremarks from tt_invoicehdr a,mg_partyhdr b \r\n"
			+ "where  a.partycode = b.party_code and approve1 = 'T' and approve1name is not null and ( approve1name=?1 or 'admin'=?1 ) "
			 + " and eligislab in (1,2,3) AND a.branchcode in (select distinct branchcode from vg_userbranch where userName = ?1) \r\n"
			 + "            and (  'ALL' = decode ( ?1 , 'admin','ALL', eligislab) or \r\n"
			 + "             eligislab = decode ( ?1 , 'admin',0, eligislab)\r\n"
			 + "             )  \r\n"
			 + "             AND a.branchcode in (select distinct branchcode from vg_userbranch where userName = ?1) order by a.createdon desc\r\n")
	Set<Object[]> getTTInvDetailsApprove1(String userName);
	
}
