package com.invoice.approval.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.TokenVO;

public interface TokenRepo extends JpaRepository<TokenVO, String> {

}
