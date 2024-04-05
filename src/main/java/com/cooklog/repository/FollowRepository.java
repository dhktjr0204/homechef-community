package com.cooklog.repository;


import com.cooklog.dto.FollowDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cooklog.model.Follow;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cooklog.dto.MyPageFollowCountDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    //유저 A와 유저 B가 서로 팔로우 관계인지 확인, 이미 팔로우 중인데 팔로우 버튼을 누르면 안된다
    //select * from follow where follower_id = followerId and following_id = followingId;
    @Query("select f from Follow f where f.follower.idx = :followerId and f.following.idx = :followingId")
    Optional<Follow> findByFollowerIdAndFollowingId(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    //특정 유저를 unfollow
    //delete from follow where follower_id = followerId and following_id = followingId;
    @Modifying
    @Query("delete from Follow f where f.follower.idx = :followerId and f.following.idx = :followingId")
    void deleteByFollowerIdAndFollowingId(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    //특정 유저의 '팔로잉' 리스트를 확인
    //select * from follow where follower_id = followerId;
    @Query("select f from Follow f where f.follower.idx = :followerId")
    Page<Follow> findByFollowerId(@Param("followerId") Long followerId, Pageable pageable);

    //특정 유저의 '팔로우' 리스트를 확인
    //select * from follow where following_id = followingId
    @Query("select f from Follow f where f.following.idx = :followingId")
    Page<Follow> findByFollowingId(@Param("followingId") Long followingId,Pageable pageable);

    //로그인한 유저가 특정 유저의 '팔로잉'리스트의 사람들을 '팔로우'한 상태인지 확인
    //특정 유저(userIdx) followingPage에 내가 팔로우한 사람들은 '팔로잉'이 뜨고 팔로우하지않은 사람들은 '팔로우'가 뜬다
    @Query("SELECT new com.cooklog.dto.FollowDTO("
        + "f.follower.idx, f.follower.nickname,f.follower.profileImage, "
        + "f.following.idx, f.following.nickname,f.following.profileImage, "
        + "(SELECT COUNT(f2) > 0 FROM Follow f2 WHERE f2.follower.idx = :currentUserIdx AND f2.following.idx = f.following.idx)"
        + ") "
        + "FROM Follow f "
        + "WHERE f.follower.idx = :userIdx")
    Page<FollowDTO> findFollowingListByUserIdxWithFollowStatus(Long userIdx, Long currentUserIdx, Pageable pageable);

    //로그인한 유저가 특정 유저의 '팔로워'리스트의 사람들을 '팔로우'한 상태인지 확인
    //특정 유저(userIdx) followerPage에 내가 팔로우한 사람들은 '팔로잉'이 뜨고 팔로우하지 않은 사람들은 '팔로우'가 뜬다
    @Query("SELECT new com.cooklog.dto.FollowDTO("
        + "f.follower.idx, f.follower.nickname,f.follower.profileImage, "
        + "f.following.idx, f.following.nickname,f.following.profileImage, "
        + "(SELECT COUNT(f2) > 0 FROM Follow f2 WHERE f2.follower.idx = :currentUserIdx AND f2.following.idx = f.follower.idx)"
        + ") "
        + "FROM Follow f "
        + "WHERE f.following.idx = :userIdx")
    Page<FollowDTO> findFollowerListByUserIdxWithFollowStatus(Long userIdx, Long currentUserIdx, Pageable pageable);
    
    //특정 유저의 following 수, follower 수, currentUser가 targetUser를 팔로우중인지 확인
    @Query(value = "SELECT " +
            "  (SELECT COUNT(*) FROM follow WHERE follower_id = :userId) AS followingCount, " +
            "  (SELECT COUNT(*) FROM follow WHERE following_id = :userId) AS followerCount,  " +
            "  CASE " +
            "    WHEN EXISTS (SELECT 1 FROM follow WHERE follower_id = :loginUserId AND following_id = :userId) THEN 1 " +
            "    ELSE 0 " +
            "  END AS isFollow", nativeQuery = true)

    MyPageFollowCountDTO findFollowCountByUserId(Long userId, Long loginUserId);

}
