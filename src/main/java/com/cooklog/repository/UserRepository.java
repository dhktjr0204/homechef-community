package com.cooklog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.dto.ReportedContentDTO;
import com.cooklog.model.Role;
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

    // 닉네임을 포함하고 reportCount가 4 이상인 사용자 검색 쿼리
    Page<User> findByNicknameContainingAndReportCountGreaterThanEqual(String nickname, int reportCount, Pageable pageable);
    // 역할(Role)에 따른 사용자 조회
    Page<User> findByRole(Role role, Pageable pageable);

    // 이메일을 포함하는 사용자 조회 (LIKE 쿼리)
    Page<User> findByEmailContaining(String email, Pageable pageable);

    // 닉네임을 포함하는 사용자 조회 (LIKE 쿼리)
    Page<User> findByNicknameContaining(String nickname, Pageable pageable);
}
