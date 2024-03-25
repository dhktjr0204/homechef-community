package com.cooklog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowDTO {
	private Long followerId;
	private Long followingId;
	private boolean isFollowing; // 팔로우 상태
}
