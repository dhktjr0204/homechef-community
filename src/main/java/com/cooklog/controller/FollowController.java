package com.cooklog.controller;

import com.cooklog.dto.FollowDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.model.User;
import com.cooklog.service.CustomIUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

		followService.follow(myId,followingUserId);
		return ResponseEntity.ok().build();
	}


	//내가 다른 유저(B)를 '언팔로우'하는 경우
	@DeleteMapping("/api/unfollow/{unfollowingUser}")
	public ResponseEntity<?> unfollow(@PathVariable long unfollowingUser) {
		Long myId = userDetailsService.getUserIdx();
		Long unfollowingUserId = unfollowingUser;

		followService.unfollow(myId,unfollowingUserId);
		return ResponseEntity.ok().build();
	}


	//특정 유저의 '팔로잉'을 확인
	@GetMapping("/api/users/{userIdx}/following")
	public String getFollowingList(
		@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable,
		@PathVariable long userIdx,Model model) {
		UserDTO currentUser = userDetailsService.getCurrentUserDTO();
		Page<FollowDTO> followingList = followService.getFollowingListWithFollowStatus(userIdx,
			currentUser.getIdx(), pageable);

		int startPage = 0;
		int endPage = 0;

		if (followingList.hasContent()) {
			// followerList가 콘텐츠를 가지고 있을 때만 페이징 계산 실행
			startPage = Math.max(0, followingList.getPageable().getPageNumber() - 4);
			endPage = Math.min( followingList.getTotalPages(), followingList.getPageable().getPageNumber() + 5);
		}
		model.addAttribute("followings", followingList);
		model.addAttribute("currentLoginUser",currentUser);
		model.addAttribute("userIdx",userIdx);
		model.addAttribute("startPage",startPage);
		model.addAttribute("endPage",endPage);
		return "myPage/followingPage";
	}


	//특정 유저의 '팔로워'를 확인
	@GetMapping("/api/users/{userIdx}/follower")
	public String getFollowerList(@PageableDefault(page = 0,size = 10, sort = "id", direction = Direction.DESC) Pageable pageable,@PathVariable long userIdx,Model model) {
		UserDTO currentUser = userDetailsService.getCurrentUserDTO();
		Page<FollowDTO> followerList = followService.getFollowerListWithFollowStatus(userIdx,
			currentUser.getIdx(), pageable);

		int startPage = 0;
		int endPage = 0;

		if (followerList.hasContent()) {
			// followerList가 콘텐츠를 가지고 있을 때만 페이징 계산 실행
			startPage = Math.max(0, followerList.getPageable().getPageNumber() - 4);
			endPage = Math.min(followerList.getTotalPages(), followerList.getPageable().getPageNumber() + 5);
		}
		model.addAttribute("followers",followerList);
		model.addAttribute("currentLoginUser",currentUser);
		model.addAttribute("userIdx",userIdx);
		model.addAttribute("startPage",startPage);
		model.addAttribute("endPage",endPage);

		return "myPage/followerPage";
//		return ResponseEntity.ok(followerList);
	}

}

