package com.cooklog.repository;

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
}

