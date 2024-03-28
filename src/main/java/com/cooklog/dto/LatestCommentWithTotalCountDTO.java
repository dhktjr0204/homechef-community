package com.cooklog.dto;

import com.cooklog.model.Comment;

import java.time.LocalDateTime;

public interface LatestCommentWithTotalCountDTO {
    Long getId();
    String getContent();
    LocalDateTime getCreatedAt();
    Long getUserId();
    String getUserName(); // 사용자 이름 또는 닉네임을 표시하기 위함
    Long getBoardId(); // 댓글이 속한 게시글 ID
    int getTotalCount();
}
