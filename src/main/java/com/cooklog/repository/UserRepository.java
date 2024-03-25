package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
