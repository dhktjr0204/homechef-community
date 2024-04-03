package com.cooklog.controller;

import com.cooklog.service.CustomIUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cooklog.service.FollowService;

@Controller
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;
	private final CustomIUserDetailsService userDetailsService;

	//내가 다른 유저(followingUser)를 '팔로우'하는 경우 = 나는 followingUser를 팔로잉 하는 상태, 나는 followingUser의 팔로워
	@PostMapping("/api/follow/{followingUser}")
	public ResponseEntity<?> follow(@PathVariable long followingUser) {
		Long myId = userDetailsService.getUserIdx();
		Long followingUserId = followingUser;

//		followService.follow(myId,followingUserId);
		return ResponseEntity.ok().build();
	}


	//내가 다른 유저(B)를 '언팔로우'하는 경우
	@DeleteMapping("/api/unfollow/{unfollowingUser}")
	public ResponseEntity<?> unfollow(@PathVariable long unfollowingUser) {
		Long myId = userDetailsService.getUserIdx();
		Long unfollowingUserId = unfollowingUser;

//		followService.unfollow(myId,unfollowingUserId);
		return ResponseEntity.ok().build();
	}


	//특정 유저의 '팔로잉'을 확인
	@GetMapping("/api/users/{userIdx}/following")
	public String getFollowingList(Pageable pageable,@PathVariable long userIdx,
		@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
		userDetailsService.isValidCurrentUser();
//		Page<FollowDTO> followingList = followService.findFollowingListByUserIdx(userIdx,pageable);

//		model.addAttribute("followings",followingList);
		return "myPage/followerPage";
	}


	//특정 유저의 '팔로워'를 확인
	@GetMapping("/api/users/{userIdx}/follower")
	public String getFollowerList(Pageable pageable,@PathVariable long userIdx,Model model) {
		userDetailsService.isValidCurrentUser();
//		Page<FollowDTO> followerList = followService.findFollowListByUserIdx(userIdx,pageable);

//		model.addAttribute("followers",followerList);
		return "myPage/followerPage";
	}

}

