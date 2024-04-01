package com.cooklog.repository;

import com.cooklog.model.Bookmark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {

    Optional<Bookmark> findByUserIdxAndBoardId(Long userIdx,Long boardId);

}
