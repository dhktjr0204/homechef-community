package com.cooklog.controller;

import com.cooklog.model.User;
import com.cooklog.service.BookmarkService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmark")
@Tag(name = "Bookmark", description = "BookmarkController")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final CustomIUserDetailsService userDetailsService;

    @Operation(summary = "특정 게시글을 북마크 등록하는 API", description = "특정 게시글에 북마크를 등록하는 API로 파라미터로 게시글의 id값을 줄 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            content = @Content(mediaType = "application/json"
            )),
        @ApiResponse(responseCode = "400",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "북마크 추가 오류", value = "{\"error\": \"해당 게시물은 이미 북마크로 등록이 되어있습니다.\"}")
            ))})
    @PostMapping("/add")
    public ResponseEntity<?> addMark(@RequestParam("boardId") Long boardId) {
        User currentUser = userDetailsService.isValidCurrentUser();
        bookmarkService.addMark(currentUser,boardId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 게시글을 북마크 취소하는 API", description = "특정 게시글에 북마크를 취소하는 API로 파라미터로 게시글의 id값을 줄 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            content = @Content(mediaType = "application/json"
            )),
        @ApiResponse(responseCode = "400",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "북마크 취소 오류", value = "{\"error\": \"해당 게시물은 이미 북마크로 등록이 되어있지 않습니다.\"}")
            ))})
    @DeleteMapping("/cancel")
    public ResponseEntity<?> cancelMark(@RequestParam("boardId") Long boardId) {
        User currentUser = userDetailsService.isValidCurrentUser();
        bookmarkService.cancelMark(currentUser,boardId);

        return ResponseEntity.ok().build();
    }
}
