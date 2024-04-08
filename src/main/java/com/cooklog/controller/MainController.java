package com.cooklog.controller;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.service.BoardService;
import com.cooklog.service.CommentService;
import com.cooklog.service.CustomIUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final CustomIUserDetailsService userDetailsService;

    @GetMapping("/")
    public String index(@PageableDefault(page = 0, size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam(value = "id", defaultValue = "0") Long lastBoardId, Model model) {

        //현재 로그인 된 유저 정보 가져오기
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        Page<BoardDTO> allBoard = boardService.getAllBoard(pageable, loginUserDTO.getIdx(), lastBoardId, pageable.getSort().toString());

        List<CommentDTO> comments = commentService.getCommentInfoByBoardId(allBoard);

        model.addAttribute("currentLoginUser", loginUserDTO);
        model.addAttribute("boards", allBoard);
        model.addAttribute("comments", comments);

        //만약 첫 요청이면 main/index를 리턴
        if (lastBoardId == 0) {
            return "main/index";
        } else {
            return "layout/boardPreview";
        }
    }


    //팔로우만 보기
    @GetMapping("/follow")
    public String getBoardWithFollow(@PageableDefault(page = 0, size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                     @RequestParam(value = "id", defaultValue = "0") Long lastBoardId, Model model) {
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        Page<BoardDTO> allBoard = boardService.getAllBoardWithFollow(pageable, loginUserDTO.getIdx(), lastBoardId);

        List<CommentDTO> comments = commentService.getCommentInfoByBoardId(allBoard);

        model.addAttribute("currentLoginUser", loginUserDTO);
        model.addAttribute("boards", allBoard);
        model.addAttribute("comments", comments);

        return "layout/boardPreview";
    }

    //텍스트 검색
    @GetMapping("/search")
    public String searchByText(@RequestParam(value = "keyword") String keyword,
                               @PageableDefault(page = 0, size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                               @RequestParam(value = "id", defaultValue = "0") Long lastBoardId,
                               Model model) {
        //현재 로그인 된 유저 정보 가져오기
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        Page<BoardDTO> searchByText = boardService.getSearchByText(keyword, loginUserDTO.getIdx(), lastBoardId, pageable);

        List<CommentDTO> comments = commentService.getCommentInfoByBoardId(searchByText);

        model.addAttribute("currentLoginUser", loginUserDTO);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boards", searchByText);
        model.addAttribute("comments", comments);

        if (lastBoardId == 0) {
            return "main/searchPage";
        } else {
            return "layout/boardPreview";
        }
    }


    //해시태그 검색
    @GetMapping("/hashtag_search")
    public String searchByhashTag(@RequestParam(value = "keyword") String tags,
                                  @PageableDefault(page = 0, size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                  @RequestParam(value = "id", defaultValue = "0") Long lastBoardId,
                                  Model model) {
        //현재 로그인 된 유저 정보 가져오기
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        Page<BoardDTO> searchByTags = boardService.findBoardsByTags(tags, loginUserDTO.getIdx(), lastBoardId, pageable);

        List<CommentDTO> comments = commentService.getCommentInfoByBoardId(searchByTags);

        model.addAttribute("currentLoginUser", loginUserDTO);
        model.addAttribute("keyword", tags);
        model.addAttribute("boards", searchByTags);
        model.addAttribute("comments", comments);

        if (lastBoardId == 0) {
            return "main/searchPage";
        } else {
            return "layout/boardPreview";
        }
    }

    @GetMapping("/access-denied")public String showAccessDenied(HttpServletRequest request) {    return "error/401";}
}
