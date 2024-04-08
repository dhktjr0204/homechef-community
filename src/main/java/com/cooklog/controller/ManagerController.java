package com.cooklog.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.services.s3.transfer.Copy;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.CommentDTO;
import com.cooklog.dto.ReportedContentDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.dto.UserPostDTO;
import com.cooklog.model.Role;
import com.cooklog.service.BlacklistService;
import com.cooklog.service.BoardService;
import com.cooklog.service.CommentService;
import com.cooklog.service.PostCombinationService;
import com.cooklog.service.ReportService;
import com.cooklog.service.UserService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.models.Swagger;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {

	private final UserService userService;
	private final BoardService boardService;
	private final CommentService commentService;
	private final ReportService reportService;
	private final BlacklistService blacklistService;
	private final PostCombinationService postCombinationService;

	/**
	 * 관리자 계정 메인페이지 API
	 * @param model 모델 객체
	 * @return 관리자 메인 페이지 경로
	 */
	@ApiOperation(value = "관리자 계정 메인페이지로 이동", notes = "11번 ID 사용자의 정보를 조회합니다. 나중에 구현 시, ID 변경 필요.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "사용자 프로필 조회 성공"),
		@ApiResponse(code = 404, message = "사용자 정보가 존재하지 않음"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/main")
	public String userProfile(Model model) {
		UserDTO userDto = userService.findUserById(11L); // 예시 ID 사용

		if (userDto != null) {
			model.addAttribute("user", userDto);
		}
		return "manager/manager";
	}

	/**
	 * 사용자 목록 조회 API
	 * @param model 모델 객체
	 * @return 사용자 관리 페이지 경로
	 */
	@ApiOperation(value = "사용자 목록 조회", notes = "시스템에 등록된 모든 사용자의 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "사용자 목록 조회 성공"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/user")
	public String listUsers(Model model) {
		List<UserDTO> users = userService.findAllUsers();
		model.addAttribute("users", users);
		return "manager/user-manager";
	}

	/**
	 * 사용자 등급 관리 API
	 * @param userId 사용자 ID
	 * @param model 모델 객체
	 * @return 사용자 등급 관리 페이지 경로
	 */
	@ApiOperation(value = "사용자 등급 관리", notes = "지정된 사용자 ID에 대한 등급 정보를 관리합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "사용자 등급 관리 페이지 접근 성공"),
		@ApiResponse(code = 404, message = "사용자 정보가 존재하지 않음"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/role-manager/{userIdx}")
	public String roleupdate(@PathVariable("userIdx") Long userId, Model model) {
		UserDTO userDto = userService.findUserById(userId);

		if (userDto != null) {
			model.addAttribute("user", userDto);
		}
		return "manager/role-manager";
	}

	/**
	 * 사용자 등급 업데이트 API
	 * @param userId 업데이트할 사용자의 ID
	 * @param roleString 새로운 등급을 나타내는 문자열
	 * @return 리다이렉트 URL
	 */
	@ApiOperation(value = "사용자 등급 업데이트", notes = "지정된 사용자의 등급을 업데이트합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "사용자 등급 업데이트 성공"),
		@ApiResponse(code = 400, message = "잘못된 요청 구조"),
		@ApiResponse(code = 404, message = "사용자 정보가 존재하지 않음"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@PostMapping("/role-manager/update")
	public String updateUserRole(@RequestParam("userIdx") Long userId, @RequestParam("role") String roleString) {
		Role role = Role.valueOf(roleString); // 문자열을 Enum으로 변환
		userService.updateUserRole(userId, role);
		return "redirect:/manager/user"; // 업데이트 후에는 등급 관리 페이지로 리다이렉트
	}

	/**
	 * 게시판 목록 조회 API
	 * @param model 모델 객체
	 * @return 게시판 관리 페이지 경로
	 */
	@ApiOperation(value = "게시판 목록 조회", notes = "시스템에 등록된 모든 게시판의 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "게시판 목록 조회 성공"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/board")
	public String listBoards(Model model) {
		List<BoardDTO> boards = boardService.findAllBoards();
		model.addAttribute("boards", boards);
		return "manager/board-manager";
	}

	/**
	 * 게시글 삭제 API
	 * @param id 삭제할 게시글의 ID
	 * @return ResponseEntity 객체를 통해 삭제 성공 여부 또는 오류 메시지 반환
	 */
	@ApiOperation(value = "게시글 삭제", notes = "지정된 게시글을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "게시글이 성공적으로 삭제됨"),
		@ApiResponse(code = 404, message = "해당 게시글이 존재하지 않음"),
		@ApiResponse(code = 500, message = "게시글 삭제 중 서버 내부 오류 발생")
	})
	@DeleteMapping("/board/delete/{id}")
	public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
		try {
			boardService.deleteBoard(id); // 게시글 삭제 서비스 호출
			return ResponseEntity.ok().build(); // 성공 응답
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류 발생");
		}
	}

	/**
	 * 댓글 목록 조회 API
	 * @param model 모델 객체
	 * @return 댓글 관리 페이지 경로
	 */
	@ApiOperation(value = "댓글 목록 조회", notes = "시스템에 등록된 모든 댓글의 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "댓글 목록 조회 성공"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/comment")
	public String listComments(Model model) {
		List<CommentDTO> comments = commentService.findAllComments();
		model.addAttribute("comments", comments);
		return "manager/comment-manager";
	}

	/**
	 * 댓글 삭제 API
	 * @param id 삭제할 댓글의 ID
	 * @return ResponseEntity 객체를 통해 삭제 성공 여부 반환
	 */
	@ApiOperation(value = "댓글 삭제", notes = "지정된 댓글을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "댓글이 성공적으로 삭제됨"),
		@ApiResponse(code = 404, message = "해당 댓글이 존재하지 않음"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@DeleteMapping("/comment/delete/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable Long id) {
		commentService.deleteComment(id);
		return ResponseEntity.ok().build();
	}

	/**
	 * 신고된 내용 조회 API
	 * @param pageable 페이지 요청 정보
	 * @param model 모델 객체
	 * @return 신고 관리 페이지 경로
	 */
	@ApiOperation(value = "신고된 내용 조회", notes = "시스템에 신고된 모든 내용을 페이지 단위로 조회합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "신고된 내용 조회 성공"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/report")
	public String showReportedContents(@PageableDefault(size = 5) Pageable pageable, Model model) {
		Page<ReportedContentDTO> reportedContentsPage = reportService.findReportedContentsPageable(pageable);
		model.addAttribute("reportedInfo", reportedContentsPage.getContent());
		model.addAttribute("currentPage", reportedContentsPage.getNumber() + 1);
		model.addAttribute("totalPages", reportedContentsPage.getTotalPages());
		return "manager/report-manager";
	}


	/**
	 * 사용자 블랙리스트 추가 API
	 * @param userId 블랙리스트에 추가할 사용자 ID
	 * @return ResponseEntity 객체를 통해 추가 성공 메시지와 상태 반환
	 */
	@ApiOperation(value = "사용자 블랙리스트 추가", notes = "지정된 사용자를 블랙리스트에 추가합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "유저를 블랙리스트에 추가하였습니다."),
		@ApiResponse(code = 404, message = "해당 사용자가 존재하지 않음"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@PostMapping("/blacklist/add/{userId}")
	public ResponseEntity<?> addToBlacklist(@PathVariable Long userId) {
		blacklistService.addToBlacklist(userId);
		boolean isBlacklisted = true;
		return ResponseEntity.ok(Map.of("message", "유저를 블랙리스트에 추가하였습니다.", "isBlacklisted", isBlacklisted));
	}

	/**
	 * 사용자 블랙리스트 제거 API
	 * @param userId 블랙리스트에서 해제할 사용자 ID
	 * @return ResponseEntity 객체를 통해 해제 성공 메시지와 상태 반환
	 */
	@ApiOperation(value = "사용자 블랙리스트 제거", notes = "지정된 사용자를 블랙리스트에서 해제합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "유저를 블랙리스트에서 해제하였습니다."),
		@ApiResponse(code = 404, message = "해당 사용자가 존재하지 않음"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@PostMapping("/blacklist/remove/{userId}")
	public ResponseEntity<?> removeFromBlacklist(@PathVariable Long userId) {
		blacklistService.removeFromBlacklist(userId);
		boolean isBlacklisted = false;
		return ResponseEntity.ok(Map.of("message", "유저를 블랙리스트에서 해제하였습니다.", "isBlacklisted", isBlacklisted));
	}

	/**
	 * 신고 페이지에 등록된 사용자 게시물 조회 API
	 * @param userId 게시물을 조회할 사용자 ID
	 * @param model 모델 객체
	 * @return 신고 페이지에 등록된 사용자의 게시물 관리 페이지 경로
	 */
	@ApiOperation(value = "신고 페이지에 등록된 사용자 게시물 조회", notes = "지정된 사용자의 게시물과 댓글을 조회합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "사용자 게시물 조회 성공"),
		@ApiResponse(code = 404, message = "해당 사용자가 존재하지 않음"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/userPosts/{userId}")
	public String showUserPosts(@PathVariable Long userId, Model model) {
		List<UserPostDTO> posts = postCombinationService.combineBoardsAndComments(userId);
		model.addAttribute("posts", posts);
		return "manager/userPosts-manager";
	}

	/**
	 * 댓글 신고 API
	 * @param commentId 신고할 댓글의 ID
	 * @return ResponseEntity 객체를 통해 신고 성공 여부 반환
	 */
	@ApiOperation(value = "댓글 신고", notes = "지정된 댓글을 신고합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "댓글 신고 성공"),
		@ApiResponse(code = 404, message = "해당 댓글이 존재하지 않음"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@PostMapping("/comment/{commentId}")
	public ResponseEntity<?> reportComment(@PathVariable Long commentId) {
		reportService.reportComment(commentId);
		return ResponseEntity.ok().build();
	}

	/**
	 * 게시판 검색 API
	 * @param category 검색 카테고리
	 * @param term 검색어
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @return ResponseEntity 객체를 통해 검색된 게시판 페이지 반환
	 */
	@ApiOperation(value = "게시판 검색", notes = "주어진 조건으로 게시판을 검색합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "게시판 검색 성공"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/boards/search")
	public ResponseEntity<Page<BoardDTO>> searchBoards(
		@RequestParam(value = "category", required = false, defaultValue = "content") String category,
		@RequestParam(value = "term", required = false, defaultValue = "") String term,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<BoardDTO> boards = boardService.searchBoards(category, term, pageable);
		return ResponseEntity.ok(boards);
	}

	/**
	 * 댓글 검색 API
	 * @param category 검색 카테고리
	 * @param term 검색어
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @return ResponseEntity 객체를 통해 검색된 댓글 페이지 반환
	 */
	@ApiOperation(value = "댓글 검색", notes = "주어진 조건으로 댓글을 검색합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "댓글 검색 성공"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/comments/search")
	public ResponseEntity<Page<CommentDTO>> searchComments(
		@RequestParam(value = "category", required = false, defaultValue = "content") String category,
		@RequestParam(value = "term", required = false, defaultValue = "") String term,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<CommentDTO> comments = commentService.searchComments(category, term, pageable);
		return ResponseEntity.ok(comments);
	}

	/**
	 * 신고된 유저 검색 기능
	 * @param nickname 검색할 닉네임
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @param model 모델 객체
	 * @return 신고 관리 페이지 경로
	 */
	@ApiOperation(value = "신고된 유저 검색", notes = "닉네임으로 신고된 유저를 검색합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "신고된 유저 검색 성공"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/reports/search")
	public String searchReportedUsers(@RequestParam("nickname") String nickname,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size, Model model) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ReportedContentDTO> reportedUsers = reportService.searchReported(nickname, pageable);
		model.addAttribute("reportedInfo", reportedUsers.getContent());
		model.addAttribute("currentPage", reportedUsers.getNumber() + 1);
		model.addAttribute("totalPages", reportedUsers.getTotalPages());
		model.addAttribute("nickname", nickname);

		return "manager/report-manager";
	}

	/**
	 * 사용자 검색 API
	 * @param category 검색 카테고리
	 * @param term 검색어
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @return ResponseEntity 객체를 통해 검색된 사용자 페이지 반환
	 */
	@ApiOperation(value = "사용자 검색", notes = "주어진 조건으로 사용자를 검색합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "사용자 검색 성공"),
		@ApiResponse(code = 500, message = "서버 내부 오류")
	})
	@GetMapping("/users/search")
	public ResponseEntity<Page<UserDTO>> searchUsers(
		@RequestParam(value = "category", required = false, defaultValue = "nickname") String category,
		@RequestParam(value = "term", required = false, defaultValue = "") String term,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("role").descending());
		Page<UserDTO> users = userService.searchUsers(category, term, pageable);
		return ResponseEntity.ok(users);
	}
}
