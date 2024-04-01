package com.cooklog.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cooklog.model.Likes;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    //select count(*) from likes where board_id = boardId  와...jpa 너무 신기해요
    Long countByBoardId(Long boardId);

    //select * from likes where user_idx = userIdx and board_id = boardId
    Optional<Likes> findByUserIdxAndBoardId(Long userIdx,Long boardId);

}

