package com.cooklog.repository;

import com.cooklog.model.Board;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

//    @Modifying(clearAutomatically = true)
//    @Query(value = "update Board b set b.readCount = b.readCount+1 where b.id = :id")
//    void updateReadCnt(Long id);

    List<Board> findByUserIdx(Long userId);

    @Query(value = "select i.board_id, i.name "
        + "from board b "
        + "join image i "
        + "on b.id = i.board_id "
        + "where b.user_idx = :userIdx and i.order = 1 "
        + "order by b.created_at desc ",nativeQuery = true)
    List<Object[]> findAllOrderByCreatedAtDesc(Long userIdx);

    //마지막 board id가 없을 경우(첫 요청일 경우)
    Page<Board> findAll(Pageable pageable);

    //두번째 요청일 경우
    @Query(value = "select b from Board b where b.id <= :id")
    Page<Board> findAllOrderByCreatedAt(Long id, Pageable pageable);

    @Query(value = "select b from Board b where b.id <= :id ")
    Page<Board> findAllOrderByReadCount(Long id, Pageable pageable);

    @Query(value = "select b from Board b where b.id<=:id order by b.likesCount desc , b.createdAt desc")
    Page<Board> findAllOrderByLikesCount(Long id, Pageable pageable);

    //팔로우된 유저들의 게시물만 보기
    @Query(value = "select b from Board b inner join Follow f on b.user.idx=f.following.idx " +
            "where f.follower.idx= :userId and b.id <=:boardId")
    Page<Board> findAllBoardWithFollow(Long userId, Long boardId, Pageable pageable);

    //첫 요청일 경우
    Page<Board> findByContentContaining(String keyword, Pageable pageable);
    //스크롤 내린 뒤 두 번째 요청일 경우
    Page<Board> findByContentContainingAndIdLessThanEqual(String keyword, Long id, Pageable pageable);


    //해당 태그가 하나라도 포함된 게시물 return, 첫요청일 경우

    @Query("SELECT t.board " +
            "FROM Board b INNER JOIN Tag t ON t.board.id=b.id " +
            "WHERE t.name IN :tagNames " +
            "GROUP BY b.id")
    Optional<Page<Board>> findBoardsByTagNames(List<String> tagNames, Pageable pageable);

    //스크롤 내린 뒤 두 번째 요청일 경우

    @Query("SELECT t.board " +
            "FROM Board b INNER JOIN Tag t ON t.board.id=b.id " +
            "WHERE t.name IN :tagNames AND b.id<=:id " +
            "GROUP BY b.id")
    Optional<Page<Board>> findBoardsByTagNamesWithLastBoardId(List<String> tagNames, Long id, Pageable pageable);

    //select count(*) from board where user_idx = userIdx
    Long countBoardByUserIdx(Long userIdx);

    Page<Board> findByUserNicknameContaining(String nickname, Pageable pageable);
}
