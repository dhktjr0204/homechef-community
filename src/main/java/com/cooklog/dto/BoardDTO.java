package com.cooklog.dto;

import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.model.Tag;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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

    //마이페이지에서 게시물의 1번째 사진만 보여줄거라서 boardId랑 image첫번째것만 가져오면 된다
    public BoardDTO(Board board,Long userIdx) {
        this.id = board.getId();
        //this.imageNames = ??
    }

    public BoardDTO(Long id, String content, String nickname, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.userNickname = nickname;
        this.createdAt = createdAt;
    }

	public BoardDTO(Long id, String content, String nickname, LocalDateTime createdAt, Integer readCount) {
        this.id = id;
        this.content = content;
        this.userNickname = nickname;
        this.createdAt = createdAt;
        this.readCount = readCount;
	}
}
