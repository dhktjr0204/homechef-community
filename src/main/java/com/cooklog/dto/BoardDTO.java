package com.cooklog.dto;

import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.model.Tag;
import java.util.stream.Collectors;
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
    private String profileImageName;
    private String profileImageUrl;
    private Long userId; // User의 식별자
    private String userNickname; // User의 닉네임
    private Boolean userIsDelete;// 탈퇴한 회원인지 식별
    private List<String> imageNames; // Image명 목록
    private List<String> imageUrls; // Image url 목록
    private List<String> tags; // 태그 이름 목록
    private Integer likeCount; // '좋아요' 개수
    private boolean isLike;
    private boolean isMarked;

    public BoardDTO(Long id, String content, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public BoardDTO(Board board,Long userIdx) {
        this.id = board.getId();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();
        this.readCount = board.getReadCount();
        this.profileImage = board.getUser().getProfileImage();
        this.userId = board.getUser().getIdx();
        this.userNickname = board.getUser().getNickname();
        this.imageNames = board.getImages().stream().map(Image::getName).collect(Collectors.toList());
        this.tags = board.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        this.likeCount = board.getLikes().size();
        this.isLike = board.getLikes().stream().anyMatch(like -> like.getUser().getIdx().equals(userIdx));
        this.isMarked = true;
    }
}
