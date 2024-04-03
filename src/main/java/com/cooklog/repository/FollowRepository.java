package com.cooklog.repository;

import com.cooklog.dto.MyPageFollowCountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cooklog.model.Follow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query(value = "SELECT " +
            "  (SELECT COUNT(*) FROM follow WHERE follower_id = :userId) AS followingCount, " +
            "  (SELECT COUNT(*) FROM follow WHERE following_id = :userId) AS followerCount,  " +
            "  CASE " +
            "    WHEN EXISTS (SELECT 1 FROM follow WHERE follower_id = :loginUserId AND following_id = :userId) THEN 1 " +
            "    ELSE 0 " +
            "  END AS isFollow", nativeQuery = true)

    MyPageFollowCountDTO findFollowCountByUserId(Long userId, Long loginUserId);

}
