package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    List<User> findAll();

    boolean existsByEmail(String email);

    User findByEmail(String email);

    //신고횟수 n회 이상인 유저 조회
    List<User> findByReportCountGreaterThan(int count);

    @Modifying
    @Query(value = "update user " +
            "set nickname = :nickname, introduction = :introduction, profile_image = :saveImagename " +
            "where idx = :userId", nativeQuery = true)
    void updateProfile(Long userId, String nickname, String introduction, String saveImagename);
}
