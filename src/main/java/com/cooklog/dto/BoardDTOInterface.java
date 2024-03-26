package com.cooklog.dto;

import java.time.LocalDateTime;

public interface BoardDTOInterface {
	Long getId();
	String getContent();
	LocalDateTime getCreatedAt();
	int getReadCount();
	Long getUserId(); // User의 식별자
	String getUserNickname(); // User의 닉네임
	String getProfileImage();
	boolean getIsLike();// 해당 User가 좋아요를 눌렀는지 유무
	String getImageUrls(); // Image의 URL 목록
	String getTags(); // 태그 이름 목록
	int getLikeCount(); // '좋아요' 개수
}
