package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Tag;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}
