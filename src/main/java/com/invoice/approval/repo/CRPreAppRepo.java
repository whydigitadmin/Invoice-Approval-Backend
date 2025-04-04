package com.invoice.approval.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.CRPreAppVO;
import com.invoice.approval.entity.IRNQRVO;




@Repository
public interface CRPreAppRepo  extends JpaRepository<CRPreAppVO, Long> {

	CRPreAppVO findByPartyCode(String partyCode);
	
	@Query(value = "select a from CRPreAppVO a where a.id=?1")
	CRPreAppVO findByGSTPreCreditrId(Long id);
	
	
	@Query(nativeQuery = true,value = "select gst_precreditId ,branchname,cramt,crremarks,invamt,partycode,partyname,profoma,ptype,reason,vchdt,\r\n"
			+ "vchno,osbcd,totdue,ddays,c.category,c.controllingoffice,c.creditlimit,c.creditdays,c.salespersonname,description,plimpact,documentsrequired from gst_precredit a ,vw_currentos b,mg_partyhdr c\r\n"
			+ "            where  approve1 = 'F' and approve1name is null and a.partycode = b.subledgercode(+) and a.partycode = c.party_code\r\n"
			+ "            AND a.branchname in (select branchname from vg_userbranch where (userName = ?1 or 'admin'= ?1 ))  order by a.createdon desc")
	Set<Object[]> getCRPendingDetailsApprove1slab1(String userName);
	
	@Query(nativeQuery = true,value = "select gst_precreditId ,branchname,cramt,crremarks,invamt,partycode,partyname,profoma,ptype,reason,vchdt,\r\n"
			+ "vchno,osbcd,totdue,ddays,c.category,c.controllingoffice,c.creditlimit,c.creditdays,c.salespersonname,description,plimpact,documentsrequired from gst_precredit a ,vw_currentos b,mg_partyhdr c\r\n"
			+ "            where  approve1 = 'T' and approve1 is not null and approve2='F' and approve2name is null and a.partycode = b.subledgercode(+) and a.partycode = c.party_code\r\n"
			+ "            AND a.branchname in (select branchname from vg_userbranch where (userName = ?1 or 'admin'= ?1 ))  order by a.createdon desc")
	Set<Object[]> getCRPendingDetailsApprove2slab1(String userName);
	
	@Query(nativeQuery = true,value = "select gst_precreditId ,branchname,cramt,crremarks,invamt,partycode,partyname,profoma,ptype,reason,vchdt, \r\n"
			+ "            vchno,osbcd,totdue,ddays,c.category,c.controllingoffice,c.creditlimit,c.creditdays,c.salespersonname,description,plimpact,documentsrequired from gst_precredit a ,vw_currentos b,mg_partyhdr c \r\n"
			+ "            where   approve1name is not null and a.partycode = b.subledgercode(+) and a.partycode = c.party_code \r\n"
			+ "            AND a.branchname in (select branchname from vg_userbranch where (userName = ?1 or 'admin'= ?1 ))  order by a.createdon desc")
	Set<Object[]> getCRApproveDetailsApprove1slab1(String userName);
	
	@Query(nativeQuery = true,value = "select gst_precreditId ,branchname,cramt,crremarks,invamt,partycode,partyname,profoma,ptype,reason,vchdt,\r\n"
			+ "vchno,osbcd,totdue,ddays,c.category,c.controllingoffice,c.creditlimit,c.creditdays,c.salespersonname,description,plimpact,documentsrequired from gst_precredit a ,vw_currentos b,mg_partyhdr c\r\n"
			+ "            where  approve1 = 'T'  and approve2='T' and approve2 is not null and approve1name is null and a.partycode = b.subledgercode(+) and a.partycode = c.party_code\r\n"
			+ "            AND a.branchname in (select branchname from vg_userbranch where (userName = ?1 or 'admin'= ?1 ))  order by a.createdon desc")
	Set<Object[]> getCRApproveDetailsApprove2slab1(String userName);
	
	@Query(nativeQuery = true,value = "select gst_cnreasonid,crreason,description,documentsrequired,plimpact from  gst_cnreason order by code")
	Set<Object[]> getCRReasons();
}

