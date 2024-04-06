package com.cooklog.service;

import com.cooklog.exception.board.BoardNotFoundException;
import com.cooklog.exception.likes.AlreadyLikedException;
import com.cooklog.exception.likes.NotLikedYetException;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Board;
import com.cooklog.model.Likes;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.cooklog.repository.LikesRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

	private final LikesRepository likesRepository;
	private final BoardRepository boardRepository;

	//특정 게시물의 총 좋아요수를 반환하는 메서드
	@Override
	public Long getNumberOfLikesByBoardId(Long boardId) {
		Board validBoard = validateBoard(boardId);

		return likesRepository.countByBoardId(validBoard.getId());
	}

	//좋아요를 저장하는 메서드
	@Transactional
	@Override
	public void addLike(User currentUser, Long boardId) {
		Board validBoard = validateBoard(boardId);

		Optional<Likes> like = likesRepository.findByUserIdxAndBoardId(currentUser.getIdx(), validBoard.getId());

		if(like.isPresent()) {
			throw new AlreadyLikedException("해당 게시물은 이미 좋아요가 눌러져 있습니다.");
		}

		Likes newLike = new Likes(currentUser,validBoard);
		likesRepository.save(newLike);
	}

	//좋아요를 취소하는 메서드
	@Transactional
	@Override
	public void cancelLike(User currentUser, Long boardId) {
		Board validBoard = validateBoard(boardId);

		Optional<Likes> like = likesRepository.findByUserIdxAndBoardId(currentUser.getIdx(), validBoard.getId());

		if(like.isEmpty()) {
			throw new NotLikedYetException("해당 게시물은 이미 좋아요가 눌러져 있지 않습니다.");
		}

		likesRepository.delete(like.get());
	}

	//유효한 boardId인지 검증하는 메서드
	private Board validateBoard(Long boardId) {
		return boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
	}

}
