package com.invoice.approval.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoice.approval.entity.DocTypeVO;

public interface DocTypeRepo extends JpaRepository<DocTypeVO, Long> {

	boolean existsByScreenCode(String screenCode);

	boolean existsByDocCode(String docCode);

	boolean existsByScreenName(String screenName);

	Optional<DocTypeVO> findByScreenCode(String screenCode);


}
