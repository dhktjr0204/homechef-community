package com.cooklog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Integer readCount;
    private String profileImage;
    private Long userId; // User의 식별자
    private String userNickname; // User의 닉네임
    private List<String> imageNames; // Image명 목록
    private List<String> imageUrls; // Image url 목록
    private List<String> tags; // 태그 이름 목록
    private Integer likeCount; // '좋아요' 개수
    private boolean isLike;

    public BoardDTO(Long id, String content, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }
}
