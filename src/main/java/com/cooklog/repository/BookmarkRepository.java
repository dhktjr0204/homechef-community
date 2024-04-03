package com.cooklog.repository;

import com.cooklog.model.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    //select * from bookmark where user_idx = userIdx and board_id = boardId;
    Optional<Bookmark> findByUserIdxAndBoardId(Long userIdx,Long boardId);

    //select * from bookmark where user_idx = userIdx;
    List<Bookmark> findAllByUserIdx(Long userIdx);
}
