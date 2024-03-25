package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cooklog.model.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
