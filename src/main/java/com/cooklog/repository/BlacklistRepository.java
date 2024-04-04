package com.cooklog.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Blacklist;
import com.cooklog.model.User;

import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
	Optional<Blacklist> findOneByUserIdx(Long userId);
	boolean existsByUserIdx(Long userId);

	boolean existsByUser(User user);

	void deleteByUser(User user);

	//creatAt을 기준으로 30일이 지난 블랙리스트 사용자를 조회하는 메소드
	List<Blacklist> findByCreatedAtBefore(LocalDateTime dateTime);
}

