package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
