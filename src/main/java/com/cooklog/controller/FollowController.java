package com.cooklog.controller;

import com.cooklog.dto.FollowDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.model.User;
import com.cooklog.service.CustomIUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.cooklog.service.FollowService;

@Controller
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;
	private final CustomIUserDetailsService userDetailsService;

	//팔로우 버튼 클릭
	@PostMapping("/api/follow/{followingUser}")
	public ResponseEntity<?> follow(@PathVariable long followingUser) {
		User currentUser = userDetailsService.isValidCurrentUser();
		Long followingUserIdx = followingUser;

		followService.follow(currentUser,followingUserIdx);
		return ResponseEntity.ok().build();
	}


	//언팔로우 버튼 클릭
	@DeleteMapping("/api/unfollow/{unfollowingUser}")
	public ResponseEntity<?> unfollow(@PathVariable long unfollowingUser) {
		User currentUser = userDetailsService.isValidCurrentUser();
		Long unfollowingUserIdx = unfollowingUser;

		followService.unfollow(currentUser,unfollowingUserIdx);
		return ResponseEntity.ok().build();
	}


	//특정 유저의 '팔로잉'을 확인
	@GetMapping("/user/{targetUserIdx}/following")
	public String getFollowingList(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable, @PathVariable long targetUserIdx,Model model) {
		UserDTO currentUser = userDetailsService.getCurrentUserDTO();
		Page<FollowDTO> followingList = followService.getFollowingListWithFollowStatus(targetUserIdx, currentUser, pageable);

		int startPage = 0;
		int endPage = 0;

		if (followingList.hasContent()) {
			startPage = Math.max(0, followingList.getPageable().getPageNumber() - 4);
			endPage = Math.min( followingList.getTotalPages() - 1, followingList.getPageable().getPageNumber() + 5);
		}

		model.addAttribute("followings", followingList);
		model.addAttribute("currentLoginUser",currentUser);
		model.addAttribute("targetUserIdx",targetUserIdx);
		model.addAttribute("startPage",startPage);
		model.addAttribute("endPage",endPage);

		return "myPage/followingPage";
	}


	//특정 유저의 '팔로워'를 확인
	@GetMapping("/user/{targetUserIdx}/follower")
	public String getFollowerList(@PageableDefault(page = 0,size = 10, sort = "id", direction = Direction.DESC) Pageable pageable,@PathVariable long targetUserIdx,Model model) {
		UserDTO currentUser = userDetailsService.getCurrentUserDTO();
		Page<FollowDTO> followerList = followService.getFollowerListWithFollowStatus(targetUserIdx, currentUser, pageable);

		int startPage = 0;
		int endPage = 0;

		if (followerList.hasContent()) {
			startPage = Math.max(0, followerList.getPageable().getPageNumber() - 4);
			endPage = Math.min(followerList.getTotalPages() - 1, followerList.getPageable().getPageNumber() + 5);
		}

		model.addAttribute("followers",followerList);
		model.addAttribute("currentLoginUser",currentUser);
		model.addAttribute("targetUserIdx",targetUserIdx);
		model.addAttribute("startPage",startPage);
		model.addAttribute("endPage",endPage);

		return "myPage/followerPage";
	}

}

