package com.cooklog.dto;

import com.cooklog.model.Follow;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowDTO {
	private Long followerId;
	private String followerUserNickName;
	private String followerUserProfileImage;
	private Long followingId;
	private String followingUserNickName;
	private String followingUserProfileImage;
	private boolean isFollowing; // 팔로우 상태

	public FollowDTO(Follow follow) {
		this.followerId = follow.getFollower().getIdx();
		this.followingId = follow.getFollowing().getIdx();
	}

	public FollowDTO(Long followerId,String followerUserNickName,String followerUserProfileImage,Long followingId,String followingUserNickName,String followingUserProfileImage,boolean isFollowing) {
		this.followerId = followerId;
		this.followerUserNickName = followerUserNickName;
		this.followerUserProfileImage = followerUserProfileImage;
		this.followingId = followingId;
		this.followingUserNickName = followingUserNickName;
		this.followingUserProfileImage = followingUserProfileImage;
		this.isFollowing = isFollowing;
	}
}
