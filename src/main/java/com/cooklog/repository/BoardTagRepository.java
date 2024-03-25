package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.BoardTag;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {
}

