package com.cooklog.controller;

import com.cooklog.model.User;
import com.cooklog.service.CustomIUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cooklog.service.LikesService;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Likes", description = "LikesController")
public class LikesController {

	private final LikesService likesService;
	private final CustomIUserDetailsService userDetailsService;

	@Operation(summary = "특정 게시글에 좋아요를 추가하는 API", description = "특정 게시글에 좋아요를 추가해서 총 좋아요수를 반환하는 API로 파라미터로 게시글의 id값을 줄 수 있습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			content = @Content(mediaType = "application/json"
			)),
		@ApiResponse(responseCode = "400",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(name = "좋아요 추가 오류", value = "{\"error\": \"해당 게시물은 이미 좋아요가 눌러져 있습니다.\"}")
			))})
	@PostMapping("/add")
	public ResponseEntity<?> addLike(@RequestParam("boardId") long boardId) {
		User currentUser = userDetailsService.isValidCurrentUser();
		likesService.addLike(currentUser, boardId);
		Long likesNum = likesService.getNumberOfLikesByBoardId(boardId);

		return ResponseEntity.ok(likesNum);
	}

	@Operation(summary = "특정 게시글에 좋아요를 취소하는 API", description = "특정 게시글에 좋아요를 취소해서 총 좋아요수를 반환하는 API로 파라미터로 게시글의 id값을 줄 수 있습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			content = @Content(mediaType = "application/json"
			)),
		@ApiResponse(responseCode = "400",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(name = "좋아요 취소 오류", value = "{\"error\": \"해당 게시물은 이미 좋아요가 눌러져 있지 않습니다.\"}")
			))})
	@DeleteMapping("/cancel")
	public ResponseEntity<?> cancelLike(@RequestParam("boardId") long boardId) {
		User currentUser = userDetailsService.isValidCurrentUser();
		likesService.cancelLike(currentUser,boardId);
		Long likesNum = likesService.getNumberOfLikesByBoardId(boardId);

		return ResponseEntity.ok(likesNum);
	}
}
