package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Blacklist;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
}

