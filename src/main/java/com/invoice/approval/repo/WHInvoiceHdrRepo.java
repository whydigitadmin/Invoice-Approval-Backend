package com.invoice.approval.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.invoice.approval.entity.WHInvoiceHdrVO;

public interface WHInvoiceHdrRepo extends JpaRepository<WHInvoiceHdrVO, Long> {

	@Query(nativeQuery = true,value = "select wh_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,"
			+ "b.creditdays,b.creditlimit,slabremarks,exceeddays,eligislab,unapproveamt,osbeyond,excesscredit,category,controllingoffice , case when lower(salesperson) like 'uwl%' then 'Mr./Ms. '||initcap(salespersonname) else initcap(salespersonname) end salespersonname,"
			+ "approve1,null approve2,null approve3,approve1name,approve1on from wh_invoicehdr a,mg_partyhdr b \r\n"
			+ "where invproceed = 'F' and approve1 = 'F' and a.partycode = b.party_code and approve1name is null"
			+ " AND eligislab = 1 AND a.branchcode in (select branchcode from vg_userbranch where (userName =?1 or 'admin'=?1 ))  order by a.createdon desc")
	Set<Object[]> getPendingDetailsApprove1slab1(String userName);
	
	
	@Query(nativeQuery = true,value = "select wh_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,"
			+ "b.creditdays,b.creditlimit,slabremarks,exceeddays,eligislab,unapproveamt,osbeyond,excesscredit,category,controllingoffice , case when lower(salesperson) like 'uwl%' then 'Mr./Ms. '||initcap(salespersonname) else initcap(salespersonname) end salespersonname,"
			+ "approve1,null approve2,null approve3,approve1name,approve1on from wh_invoicehdr a,mg_partyhdr b \r\n"
			+ "where invproceed = 'F' and approve1 = 'F' and a.partycode = b.party_code and approve1name is null"
			+ " AND eligislab in (2,3) AND a.branchcode in (select branchcode from vg_userbranch where (userName =?1 or 'admin'=?1 ))  order by a.createdon desc")
	Set<Object[]> getPendingDetailsApprove1slab2(String userName);
	

	@Query(nativeQuery = true,value = "select wh_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,"
			+ "b.creditdays,b.creditlimit,slabremarks,exceeddays,eligislab,unapproveamt,osbeyond,excesscredit,category,controllingoffice , case when lower(salesperson) like 'uwl%' then 'Mr./Ms. '||initcap(salespersonname) else initcap(salespersonname) end salespersonname,"
			+ "approve1,null approve2,null approve3,approve1name,approve1on from wh_invoicehdr a,mg_partyhdr b \r\n"
			+ "where invproceed = 'F' and approve1 = 'F' and a.partycode = b.party_code and approve1name is null"
			+ " AND eligislab in (1,2,3) AND a.branchcode in (select branchcode from vg_userbranch where (userName =?1 or 'admin'=?1 ))  order by a.createdon desc")
	Set<Object[]> getAdminPendingDetailsApprove1slab(String userName);

	@Query(value = "select a from WHInvoiceHdrVO a where a.WHInvoiceHdrId=?1")
	WHInvoiceHdrVO findByWHInvoiceHdrId(Long id);
	
	
	
	
	@Query(nativeQuery = true,value = "select wh_invoicehdrid,a.branchcode,finyr,docid,docdt,Partyname,partycode,outstanding,totinvamtlc,b.creditdays,b.creditlimit,unapproveamt,approve1,null approve2,null approve3,approve1name,approve1on,null approve2name,null approve2on,null approve3on,osbeyond,excesscredit,category,controllingoffice,case when lower(salesperson) like 'uwl%' then 'Mr./Ms. '||initcap(salespersonname) else initcap(salespersonname) end salespersonname,slabremarks from wh_invoicehdr a,mg_partyhdr b \r\n"
			+ "where  a.partycode = b.party_code and approve1 = 'T' and approve1name is not null and ( approve1name=?1 or 'admin'=?1 ) "
			 + " and eligislab in (1,2,3) AND a.branchcode in (select distinct branchcode from vg_userbranch where userName = ?1) \r\n"
			 + "            and (  'ALL' = decode ( ?1 , 'admin','ALL', eligislab) or \r\n"
			 + "             eligislab = decode ( ?1 , 'admin',0, eligislab)\r\n"
			 + "             )  \r\n"
			 + "             AND a.branchcode in (select distinct branchcode from vg_userbranch where userName = ?1)\r\n")
	Set<Object[]> getWHInvDetailsApprove1(String userName);
	
}
