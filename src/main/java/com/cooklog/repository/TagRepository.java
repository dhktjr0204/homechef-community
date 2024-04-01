package com.cooklog.repository;

import com.cooklog.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    void deleteByBoard_Id(Long boardId);
}
