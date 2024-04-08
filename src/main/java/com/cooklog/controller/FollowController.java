package com.cooklog.controller;

import com.cooklog.dto.FollowDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.model.User;
import com.cooklog.service.CustomIUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Follow", description = "FollowController")
public class FollowController {

	private final FollowService followService;
	private final CustomIUserDetailsService userDetailsService;

	//팔로우 버튼 클릭
	@Operation(summary = "특정 유저를 팔로우하는 API", description = "특정 유저를 팔로우하는 API로 파라미터로 팔로잉할 유저의 id값을 줄 수 있습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			content = @Content(mediaType = "application/json"
			)),
		@ApiResponse(responseCode = "400",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(name = "팔로우 추가 오류", value = "{\"error\": \"자기 자신을 팔로우 할 수 없습니다.\"}"),
					@ExampleObject(name = "팔로우 추가 오류", value = "{\"error\": \"이미 팔로우 중인 유저입니다.\"}")
				}
			))})
	@PostMapping("/api/follow/{followingUser}")
	public ResponseEntity<?> follow(@PathVariable long followingUser) {
		User currentUser = userDetailsService.isValidCurrentUser();
		Long followingUserIdx = followingUser;

		followService.follow(currentUser,followingUserIdx);
		return ResponseEntity.ok().build();
	}


	//언팔로우 버튼 클릭
	@Operation(summary = "특정 유저를 언팔로우하는 API", description = "특정 유저를 언팔로우하는 API로 파라미터로 팔로잉할 유저의 id값을 줄 수 있습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			content = @Content(mediaType = "application/json"
			)),
		@ApiResponse(responseCode = "400",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(name = "팔로우 추가 오류", value = "{\"error\": \"자기 자신을 언팔로우 할 수 없습니다.\"}"),
					@ExampleObject(name = "팔로우 추가 오류", value = "{\"error\": \"이미 언팔로우 중인 유저입니다.\"}")
				}
			))})
	@DeleteMapping("/api/unfollow/{unfollowingUser}")
	public ResponseEntity<?> unfollow(@PathVariable long unfollowingUser) {
		User currentUser = userDetailsService.isValidCurrentUser();
		Long unfollowingUserIdx = unfollowingUser;

		followService.unfollow(currentUser,unfollowingUserIdx);
		return ResponseEntity.ok().build();
	}


	//특정 유저의 '팔로잉'을 확인
	@Operation(summary = "특정 유저의 팔로잉 리스트를 조회하는 API", description = "특정 유저의 팔로잉 리스트를 조회하는 API로 파라미터로 조회할 유저의 id값을 줄 수 있습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			content = @Content(mediaType = "text/html"
			))
	})
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
	@Operation(summary = "특정 유저의 팔로워 리스트를 조회하는 API", description = "특정 유저의 팔로워 리스트를 조회하는 API로 파라미터로 조회할 유저의 id값을 줄 수 있습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			content = @Content(mediaType = "text/html"
			))
	})
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

