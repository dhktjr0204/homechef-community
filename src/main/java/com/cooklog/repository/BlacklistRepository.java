package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Blacklist;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
}

