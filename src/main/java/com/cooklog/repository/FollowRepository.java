package com.cooklog.repository;


import com.cooklog.dto.FollowDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cooklog.model.Follow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cooklog.dto.MyPageFollowCountDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {


    //예외처리) 유저 A와 유저 B가 서로 팔로우 관계인지 확인, 이미 팔로우 중인데 팔로우 버튼을 누르면 안된다
    //결과는 단 1개가 Follow가 return 되거나 null이 나온다
    //select * from follow where follower_id = followerId and following_id = followingId;
//    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId,Long followingId);
//
//    //특정 유저를 unfollow
//    //delete from follow where follower_id = followerId and following_id = followingId;
//    void deleteByFollowerIdAndFollowingId(Long followerId,Long followingId);

    //특정 유저의 '팔로잉' 리스트를 확인
    //select * from follow where follower_id = followerId;
//    Page<Follow> findByFollowerId(Long followerId, Pageable pageable);

    //특정 유저의 '팔로우' 리스트를 확인
    //select * from follow where following_id = followingId
//    Page<Follow> findByFollowingId(Long followingId,Pageable pageable);

    //로그인한 유저가 특정 유저의 '팔로잉'리스트의 사람들을 '팔로우'한 상태인지 확인
    //특정 유저(userIdx) followingPage에 내가 팔로우한 사람들은 '팔로잉'이 뜨고 팔로우하지않은 사람들은 '팔로우'가 뜬다
    @Query("SELECT new com.cooklog.dto.FollowDTO(" +
        "f.follower.idx, f.follower.nickname, " +
        "f.following.idx, f.following.nickname, " +
        "(CASE WHEN fc.id IS NOT NULL THEN true ELSE false END)) " +
        "FROM Follow f " +
        "LEFT JOIN Follow fc ON f.following.idx = fc.following.idx AND fc.follower.idx = :currentUserIdx " +
        "WHERE f.follower.idx = :userIdx")
    Page<FollowDTO> findFollowingListByUserIdxWithFollowStatus(@Param("userIdx") Long userIdx,@Param("currentUserIdx") Long currentUserIdx, Pageable pageable);

    //로그인한 유저가 특정 유저의 '팔로워'리스트의 사람들을 '팔로우'한 상태인지 확인
    //특정 유저(userIdx) followerPage에 내가 팔로우한 사람들은 '팔로잉'이 뜨고 팔로우하지 않은 사람들은 '팔로우'가 뜬다
    @Query("SELECT new com.cooklog.dto.FollowDTO(" +
        "f.follower.idx, f.follower.nickname, " +
        "f.following.idx, f.following.nickname," +
        "(CASE WHEN fc.id IS NOT NULL THEN true ELSE false END)) " +
        "FROM Follow f " +
        "LEFT JOIN Follow fc ON f.follower.idx = fc.follower.idx AND fc.follower.idx = :currentUserIdx " +
        "WHERE f.following.idx = :userIdx")
    Page<FollowDTO> findFollowerListByUserIdxWithFollowStatus(@Param("userIdx") Long userIdx,@Param("currentUserIdx") Long currentUserIdx, Pageable pageable);

    @Query(value = "SELECT " +
            "  (SELECT COUNT(*) FROM follow WHERE follower_id = :userId) AS followingCount, " +
            "  (SELECT COUNT(*) FROM follow WHERE following_id = :userId) AS followerCount,  " +
            "  CASE " +
            "    WHEN EXISTS (SELECT 1 FROM follow WHERE follower_id = :loginUserId AND following_id = :userId) THEN 1 " +
            "    ELSE 0 " +
            "  END AS isFollow", nativeQuery = true)

    MyPageFollowCountDTO findFollowCountByUserId(Long userId, Long loginUserId);

}
