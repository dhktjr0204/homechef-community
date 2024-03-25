package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Board;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
}
