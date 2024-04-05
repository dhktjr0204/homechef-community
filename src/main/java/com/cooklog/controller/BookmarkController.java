package com.cooklog.controller;

import com.cooklog.model.User;
import com.cooklog.service.BookmarkService;
import com.cooklog.service.CustomIUserDetailsService;
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
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final CustomIUserDetailsService userDetailsService;

    @PostMapping("/add")
    public ResponseEntity<?> addMark(@RequestParam("boardId") Long boardId) {
        User currentUser = userDetailsService.isValidCurrentUser();
        bookmarkService.addMark(currentUser,boardId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<?> cancelMark(@RequestParam("boardId") Long boardId) {
        User currentUser = userDetailsService.isValidCurrentUser();
        bookmarkService.cancelMark(currentUser,boardId);

        return ResponseEntity.ok().build();
    }
}
