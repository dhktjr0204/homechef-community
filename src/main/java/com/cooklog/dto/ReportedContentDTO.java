package com.cooklog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportedContentDTO {
	private String userNickname; // 신고된 사용자의 닉네임
	private int reportCount; // 해당 사용자의 신고당한 횟수
	private Long userId; // 신고된 사용자의 ID
	private boolean isBlacklisted;


	// 모든 필드를 초기화하는 생성자
	public ReportedContentDTO(String userNickname,int reportCount, Long userId, boolean isBlacklisted) {
		this.userNickname = userNickname;
		this.reportCount = reportCount;
		this.userId = userId;
		this.isBlacklisted = isBlacklisted;
	}

	public boolean isBlacklisted() {
		return isBlacklisted;
	}
}
