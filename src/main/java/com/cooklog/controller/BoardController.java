package com.cooklog.controller;

import com.cooklog.dto.BoardCreateRequestDTO;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Board;
import com.cooklog.service.BoardService;
import com.cooklog.service.CommentService;
import com.cooklog.service.CustomIUserDetailsService;
import com.cooklog.service.ReportService;
import com.cooklog.validate.BoardCreateValidator;
import com.cooklog.validate.BoardUpdateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
    private final BoardService boardService;
    private final CommentService commentService;
    private final CustomIUserDetailsService userDetailsService;
    private final ReportService reportService;

    @GetMapping("/{id}")
    @Operation(summary = "게시물을 조회할 수 있는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 HTML을 반환",
                content = @Content(mediaType = "text/html"))
    })
    public String getBoard(@PathVariable Long id, Model model) {
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        Board findBoard = boardService.getBoard(id, loginUserDTO.getIdx());

        //조회수 업데이트
        boardService.updateReadCnt(findBoard);
        BoardDTO board=boardService.convertBoardToDTO(findBoard,loginUserDTO.getIdx());

        model.addAttribute("currentLoginUser", loginUserDTO);
        model.addAttribute("board", board);

        return "board/board";
    }

    @GetMapping("/write")
    @Operation(summary = "게시물을 작성할 수 있는 페이지 조회 API", description = "유저가 게시물을 작성할 수 있는 페이지로 이동하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 작성 form HTML 반환",
                content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력 값",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name="존재하지 않는 게시물 수정 요청")))
    })
    public String getWriteForm(Model model) {
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        model.addAttribute("currentLoginUser", loginUserDTO);
        model.addAttribute("board", new BoardCreateRequestDTO());

        return "board/boardForm";
    }

    @PostMapping("/write")
    @Operation(summary = "게시물 작성 API", description = "유저가 게시물을 등록하면 내용들이 DB로 저장되고 자신이 작성한 게시글로 이동합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 작성한 게시물 HTML반환",
                content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 유저 요청",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name="인증되지 않은 유저 오류", value ="인증되지 않은 사용자입니다" ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력 값",
                content = @Content(mediaType = "application/json",
                    examples = {
                        @ExampleObject(name="게시글 본문 입력 오류", value ="콘텐츠 길이가 최대 길이를 초과하였습니다." ),
                        @ExampleObject(name="게시물 태그 입력 오류", value ="태그가 최대 개수를 초과하였습니다." ),
                        @ExampleObject(name="게시물 태그 입력 오류", value ="태그 길이가 최대 길이를 초과하였습니다. 태크 길이를 줄여주세요." )
                }))
    })
    public ResponseEntity<?> save(BoardCreateRequestDTO boardCreateRequestDTO,
                                  @RequestPart("images") List<MultipartFile> images,
                                  BindingResult result) {

        // 현재 인증된(로그인한) 사용자의 idx 가져와서 userDTO에 할당
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        //만약 탈퇴한 회원이라면 예외처리
        if(loginUserDTO.isDeleted()){
            throw new NotValidateUserException();
        }

        //저장할 요소들 범위 넘지 않는지 확인하는 validate
        BoardCreateValidator boardCreateValidator = new BoardCreateValidator();
        boardCreateValidator.validate(boardCreateRequestDTO, result);

        Board board = boardService.save(loginUserDTO.getIdx(), boardCreateRequestDTO, images);

        return ResponseEntity.ok("/board/" + board.getId());
    }

    @GetMapping("/edit/{id}")
    @Operation(summary = "게시물을 수정할 수 있는 페이지 조회 API", description = "유저가 등록한 게시물을 수정할 수 있는 페이지를 조회하는 API입니다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "게시물 수정 form HTML을 반환",
                content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 유저 요청",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name="인증되지 않은 유저 오류", value ="인증되지 않은 사용자입니다" ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력 값",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name="존재하지 않는 게시물 수정 요청")))
    })
    public String getEditForm(@PathVariable Long id, Model model) {

        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        Board findBoard = boardService.getBoard(id, loginUserDTO.getIdx());
        BoardDTO board=boardService.convertBoardToDTO(findBoard,loginUserDTO.getIdx());

        //만약 인증되지 않은 사용자라면 404페이지 리턴
        if (!board.getUserId().equals(loginUserDTO.getIdx()) || loginUserDTO.isDeleted()) {
            return "error/404";
        }

        model.addAttribute("currentLoginUser", loginUserDTO);
        model.addAttribute("board", board);

        return "board/boardEditForm";
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "게시물을 수정할 수 있는 API", description = "유저가 등록한 게시물을 수정할 수 있는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정된 게시물 HTML반환",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 유저 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name="인증되지 않은 유저 오류", value ="인증되지 않은 사용자입니다" ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력 값",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name="게시글 본문 입력 오류", value ="콘텐츠 길이가 최대 길이를 초과하였습니다." ),
                                    @ExampleObject(name="게시물 태그 입력 오류", value ="태그가 최대 개수를 초과하였습니다." ),
                                    @ExampleObject(name="게시물 태그 입력 오류", value ="태그 길이가 최대 길이를 초과하였습니다. 태크 길이를 줄여주세요." )
                            }))
    })
    public ResponseEntity<?> edit(@PathVariable Long id, BoardUpdateRequestDTO boardUpdateRequestDTO,
                                  @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                  BindingResult result) {

        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        //만약 인증되지 않은 사용자거나 탈퇴한 사용자라면 예외처리
        if (!boardUpdateRequestDTO.getUserId().equals(loginUserDTO.getIdx()) || loginUserDTO.isDeleted()) {
            throw new NotValidateUserException();
        }

        //저장할 요소들 범위 넘지 않는지 확인하는 validate
        BoardUpdateValidator boardUpdateValidator=new BoardUpdateValidator();
        boardUpdateValidator.validate(boardUpdateRequestDTO, result);

        Board board = boardService.updateBoard(id, boardUpdateRequestDTO, boardUpdateRequestDTO.getImageUrls(), images);

        return ResponseEntity.ok("/board/" + id);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "게시물을 삭제할 수 있는 API", description = "유저가 등록한 게시물을 삭제할 수 있는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메인 HTML을 반환",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 유저 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name="인증되지 않은 유저 오류", value ="인증되지 않은 사용자입니다" ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력 값",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name="존재하지 않는 게시물 수정 요청")))
    })
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestParam("userId") Long userId) {
        UserDTO loginUserDTO = userDetailsService.getCurrentUserDTO();

        if (!userId.equals(loginUserDTO.getIdx()) || loginUserDTO.isDeleted()) {
            throw new NotValidateUserException();
        }

        boardService.deleteBoard(id);
        return ResponseEntity.ok("/");
    }

    //댓글 추가
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long boardId, @RequestBody CommentDTO commentDTO) {
        CommentDTO savedComment = commentService.addComment(boardId, commentDTO);
        return ResponseEntity.ok(savedComment);
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO) {
        CommentDTO updatedComment = commentService.updateComment(commentId, commentDTO);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{boardId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit) {

        Page<CommentDTO> commentPage = commentService.getCommentsByBoardId(boardId, page, limit);
        return ResponseEntity.ok(commentPage);
    }

    @GetMapping("/{parentId}/replies")
    public ResponseEntity<List<CommentDTO>> getRepliesByCommentId(
            @PathVariable Long parentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        List<CommentDTO> replies = commentService.getRepliesByCommentId(parentId, page, size);
        return ResponseEntity.ok(replies);
    }
    @PostMapping("/reportBoard/{boardId}")
    public ResponseEntity<?> reportBoard(@PathVariable Long boardId) {
        logger.info("Reporting board with ID: " + boardId);
        reportService.reportBoard(boardId);
        return ResponseEntity.ok().build();
    }
}

