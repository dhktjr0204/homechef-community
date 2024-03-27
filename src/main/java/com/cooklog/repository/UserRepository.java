package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Optional<User> findByEmail(String email); //이메일로 사용자 정보 가져오기

    @Override
    List<User> findAll();

    boolean existsByEmail(String email);

    User findByEmail(String email);
}
