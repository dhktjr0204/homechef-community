package com.cooklog.repository;

import com.cooklog.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Image;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<List<Image>> findAllByBoard_IdOrderByOrder(Long boardId);
}

