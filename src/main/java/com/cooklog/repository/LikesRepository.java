package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cooklog.model.Likes;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
}

