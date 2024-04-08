package com.cooklog.controller;

import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.service.BoardService;
import com.cooklog.service.CommentService;
import com.cooklog.service.CustomIUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final CustomIUserDetailsService userDetailsService;

    @GetMapping("/")
    @Operation(summary = "메인페이지 조회 API", description = "메인 페이지를 조회하는 API며, 페이징을 포함합니다."
            +"페이지 당 3개씩 반환하며, 첫요청 제회 lastBoardId 값을 넘겨주어야합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "첫 요청일 경우 메인 페이지, 두 번째 요청부터는 게시물목록 HTML 반환",
                    content = @Content(mediaType = "text/html"))
    })
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
    @Operation(summary = "유저의 팔로우 게시물 목록 조회 API", description = "로그인한 유저가 팔로우한 유저들의 게시물 목록 조회하는 API며, 페이징을 포함합니다."
            +"페이지 당 3개씩 반환하며, lastBoardId 값을 같이 넘겨주어야합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 목록 HTML 반환",
                    content = @Content(mediaType = "text/html"))
    })
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
    @Operation(summary = "게시물의 내용을 검색한 결과를 조회하는 API", description = "게시물 제목 검색 결과를 조회하는 API이며, 페이징을 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "첫 요청일 경우 메인 페이지, 두 번째 요청부터는 게시물목록 HTML 반환",
                content = @Content(mediaType = "text/html"))
    })
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
    @Operation(summary = "태그를 포함한 게시물들을 조회하는 API", description = "특정 태그를 하나라도 포함하는 게시물들을 조회하는 API이며, 페이징을 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "첫 요청일 경우 메인 페이지, 두 번째 요청부터는 게시물목록 HTML 반환",
                    content = @Content(mediaType = "text/html"))
    })
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
