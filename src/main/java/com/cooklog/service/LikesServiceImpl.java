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
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;

	@Override
	public Long getNumberOfLikesByBoardId(Long boardId) {
		Board validBoard = validateBoard(boardId);

		return likesRepository.countByBoardId(validBoard.getId());
	}

	//특정 사용자가 특정 게시물에 좋아요를 누른 상태인지 아닌지를 검사하는 메소드
	@Override
	public boolean existsByUserIdAndBoardId(Long userIdx, Long boardId) {
		Board validBoard = validateBoard(boardId);

		Optional<Likes> like = likesRepository.findByUserIdxAndBoardId(userIdx,validBoard.getId());

		if(like.isPresent()) {//사용자가 특정 게시물에 좋아요를 누른 상태라면
			return true;
		}

		return false;
	}

	@Transactional
	@Override
	public void addLike(Long userIdx, Long boardId) {
		User validUser = validateUser(userIdx);
		Board validBoard = validateBoard(boardId);
		Optional<Likes> like = likesRepository.findByUserIdxAndBoardId(validUser.getIdx(),validBoard.getId());

		if(like.isPresent()) {
			throw new AlreadyLikedException();
		}

		Likes newLike = new Likes(validUser,validBoard);
		likesRepository.save(newLike);

	}

	@Transactional
	@Override
	public void cancelLike(Long userIdx, Long boardId) {
		User validUser = validateUser(userIdx);
		Board validBoard = validateBoard(boardId);
		Optional<Likes> like = likesRepository.findByUserIdxAndBoardId(validUser.getIdx(),validBoard.getId());

		if(like.isEmpty()) {
			throw new NotLikedYetException();
		}

		likesRepository.delete(like.get());
	}

	private User validateUser(Long userIdx) {
		return userRepository.findById(userIdx).orElseThrow(NotValidateUserException::new);
	}

	private Board validateBoard(Long boardId) {
		return boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
	}

}
