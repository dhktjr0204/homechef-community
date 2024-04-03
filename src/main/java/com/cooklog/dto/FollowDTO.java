package com.cooklog.dto;

import com.cooklog.model.Follow;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class FollowDTO {
	private Long followerId;
	private String followerUserNickName;
	private Long followingId;
	private String followingUserNickName;
	private boolean isFollowing; // 팔로우 상태

	public FollowDTO(Follow follow) {
		this.followerId = follow.getFollower().getIdx();
		this.followingId = follow.getFollowing().getIdx();
	}

	public FollowDTO(Long followerId,String followerUserNickName,Long followingId,String followingUserNickName,boolean isFollowing) {
		this.followerId = followerId;
		this.followerUserNickName = followerUserNickName;
		this.followingId = followingId;
		this.followingUserNickName = followingUserNickName;
		this.isFollowing = isFollowing;
	}
}
