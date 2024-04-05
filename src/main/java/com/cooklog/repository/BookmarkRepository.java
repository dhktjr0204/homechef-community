package com.cooklog.repository;

import com.cooklog.model.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {

    //특정 유저가 특정 게시물을 북마크로 등록해놓았는지 확인
    //select * from bookmark where user_idx = userIdx and board_id = boardId;
    Optional<Bookmark> findByUserIdxAndBoardId(Long userIdx,Long boardId);

    //특정 유저가 등록해둔 모든 북마크들을 가져온다
    //select * from bookmark where user_idx = userIdx;
    List<Bookmark> findAllByUserIdx(Long userIdx);
}
