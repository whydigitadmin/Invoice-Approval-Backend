package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.PartyHdrVO;
import com.invoice.approval.entity.PartyMasterVO;

@Repository
public interface PartyMasterRepo extends JpaRepository<PartyMasterVO, Long> {

	PartyMasterVO findByPartyCode(String partyCode);

}