package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
