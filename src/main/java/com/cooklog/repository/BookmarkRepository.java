package com.cooklog.repository;

import com.cooklog.model.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {

    //특정 유저가 특정 게시물을 북마크로 등록해놓았는지 확인
    //select * from bookmark where user_idx = userIdx and board_id = boardId;
    Optional<Bookmark> findByUserIdxAndBoardId(Long userIdx,Long boardId);

    //특정 유저가 등록해둔 모든 북마크들을 가져온다
    //select * from bookmark where user_idx = userIdx order by id desc;
    List<Bookmark> findAllByUserIdxOrderByIdDesc(Long userIdx);

    //특정 유저가 북마크 등록해둔 모든 게시물들의 첫번째 이미지를 가져온다
    @Query(value = "select i.board_id, i.name "
        + "from (select bm.board_id, bm.id from bookmark as bm "
        + "join board b on bm.board_id = b.id "
        + "where bm.user_idx = :userIdx "
        + "order by bm.id desc) as rs "
        + "join image i "
        + "on rs.board_id = i.board_id "
        + "where i.order = 1 "
        + "order by rs.id desc",nativeQuery = true)
    List<Object[]> findAllBookmarkedBoardsByUserIdx(Long userIdx);
}
