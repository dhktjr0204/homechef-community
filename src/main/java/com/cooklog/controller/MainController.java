package com.cooklog.controller;

import com.cooklog.dto.BoardDTO;
import com.cooklog.service.BoardService;
import com.cooklog.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cooklog.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    private final BoardService boardService;
    private final ImageService imageService;

    @GetMapping("")
    public String index(@PageableDefault(page = 0, size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam( value = "id", defaultValue = "0") Long lastBoardId, Model model) throws FileNotFoundException {
        long userId = 1;

        Page<BoardDTO> allBoard = boardService.getAllBoard(pageable, userId, lastBoardId);
        List<List<String>> allImage = imageService.getAllFileListLoad(allBoard);

        model.addAttribute("boards", allBoard);
        model.addAttribute("images", allImage);

        //만약 첫 요청이면 main/index를 리턴
        if (pageable.getPageNumber() == 0) {
            return "main/index";
        }else{
            return "layout/boardPreview";
        }

//		return ResponseEntity.ok(allBoard);
    }
}
