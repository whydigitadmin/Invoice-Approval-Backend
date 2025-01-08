package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoice.approval.entity.PartyHdrVO;

@Repository
public interface PartyHdrRepo extends JpaRepository<PartyHdrVO, Long> {

}