package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cooklog.model.Follow;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
}
