package com.cooklog.repository;


import java.util.Collection;
import java.util.List;
import com.cooklog.dto.LatestCommentWithTotalCountDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cooklog.model.Board;
import com.cooklog.model.Comment;


import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


	List<Comment> findByUserIdx(Long userId);

    //최근 댓글 하나와 전체 댓글 개수
    @Query(value = "SELECT c.id, c.board_id as boardId, c.user_idx as userId, c.content, c.created_at as createdAt, u.nickname as userName, " +
            "(SELECT COUNT(*) FROM comment sub WHERE sub.parent_comment_id= :parentCommentId AND sub.board_id= :boardId) AS totalCount " +
            "FROM comment c " +
            "INNER JOIN user u ON c.user_idx=u.idx " +
            "WHERE c.parent_comment_id= :parentCommentId AND c.board_id= :boardId " +
            "ORDER BY c.created_at DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<LatestCommentWithTotalCountDTO> findLatestCommentByBoardId(@Param("boardId") Long boardId, @Param("parentCommentId") Long parentCommentId);

	List<Comment> findByBoardId(Long boardId);
	Page<Comment> findByBoardId(Long boardId, Pageable pageable);

	Page<Comment> findByParentCommentId(Long parentId, Pageable pageable);

}
