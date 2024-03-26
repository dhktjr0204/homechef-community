package com.cooklog.repository;

import com.cooklog.dto.BoardDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Board;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query(value = "select b.id , b.content, b.created_at as createdAt, b.readcnt AS readCount, u.idx AS userId, u.nickname AS userNickname, u.profile_image as profileImage, " +
            "case when EXISTS (SELECT 1 FROM likes WHERE board_id=b.id AND user_idx= :userId) then 1 ELSE 0 end AS isLike, " +
            "(SELECT COUNT(*) FROM likes WHERE board_id=b.id) AS likeCount, " +
            "GROUP_CONCAT(DISTINCT i.name ORDER BY `order`) AS imageUrls, " +
            "GROUP_CONCAT(DISTINCT t.name ORDER BY t.id) AS tags " +
            "FROM board b " +
            "LEFT JOIN user u ON b.user_idx=u.idx " +
            "LEFT JOIN likes l ON b.id=l.board_id " +
            "LEFT JOIN image i ON b.id=i.board_id " +
            "LEFT JOIN tag t ON b.id=t.board_id " +
            "WHERE b.id= :boardId " +
            "GROUP BY b.id ", nativeQuery = true)
    Optional<BoardDTO> findByBoardIdAndUserId(@Param("boardId") Long BoardId, @Param("userId") Long userId);

    @Modifying
    @Query(value = "update board set readcnt = readcnt+1 where id = :id", nativeQuery = true)
    void updateReadCnt(Long id);
}
