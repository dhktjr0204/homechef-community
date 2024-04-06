package com.cooklog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT COUNT(b) FROM Board b WHERE b.user.idx = :userId")
    int countPostsByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(b.likesCount), 0) FROM Board b WHERE b.user.idx = :userId")
    int sumLikesByUserId(@Param("userId") Long userId);

    // 닉네임에 특정 키워드가 포함된 사용자를 찾는 메서드
    List<User> findByNicknameContaining(String term);
}
