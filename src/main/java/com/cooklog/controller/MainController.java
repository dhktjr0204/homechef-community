package com.cooklog.controller;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.service.BoardService;
import com.cooklog.service.CommentService;
import com.cooklog.service.ImageService;
import com.cooklog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    private final BoardService boardService;
    private final ImageService imageService;
    private final CommentService commentService;

    @GetMapping("/")
    public String index(@PageableDefault(page = 0, size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam(value = "id", defaultValue = "0") Long lastBoardId, Model model) throws FileNotFoundException {
        long userId = 1;

        Page<BoardDTO> allBoard = boardService.getAllBoard(pageable, userId, lastBoardId, pageable.getSort().toString());

        //allBoard에 이미지 링크 추가
        Page<BoardDTO> allBoardContainsImageUrls = imageService.getAllFileListLoad(allBoard);

        List<CommentDTO> comments = commentService.getCommentInfoByBoardId(allBoard);

        model.addAttribute("boards", allBoardContainsImageUrls);
        model.addAttribute("comments", comments);

        //만약 첫 요청이면 main/index를 리턴
        if (lastBoardId == 0) {
            return "main/index";
        } else {
            return "layout/boardPreview";
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword") String keyword,
                         @PageableDefault(page = 0, size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam(value = "id", defaultValue = "0") Long lastBoardId,
                         Model model) throws FileNotFoundException {
        long userId = 1;

        Page<BoardDTO> searchByText = boardService.getSearchByText(keyword, userId, pageable);
        //allBoard에 이미지 링크 추가
        Page<BoardDTO> allBoardContainsImageUrls = imageService.getAllFileListLoad(searchByText);

        List<CommentDTO> comments = commentService.getCommentInfoByBoardId(searchByText);

        model.addAttribute("keyword", keyword);
        model.addAttribute("boards", allBoardContainsImageUrls);
        model.addAttribute("comments", comments);

        if (lastBoardId == 0) {
            return "main/searchPage";
        }else{
            return "layout/boardPreview";
        }
    }

}
