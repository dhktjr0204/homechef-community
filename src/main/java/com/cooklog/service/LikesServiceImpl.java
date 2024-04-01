package com.cooklog.service;

import com.cooklog.dto.LikesDTO;
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
		//likesRepository.findAllById(boardId).size()를 쓰면 boardId를 기준으로 모든 likes엔티티를 반환할 줄 알았지만 실제로는 likedId를 기준으로 엔티티를 반환한다
		//또한 findAllById는 기본적으로 Iterable<ID> 타입을 인자로 받음 ..
		//boardId에 따른 좋아요 수를 조회하고자 할때는 count를 직접적으로 해야한다.
		//게시물이 존재하지 않으면 어쩌지?
		Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("해당 게시글은 존재하지 않는 게시글입니다."));

		return likesRepository.countByBoardId(boardId);
	}

	//특정 사용자가 특정 게시물에 좋아요를 누른 상태인지 아닌지를 검사하는 메소드
	@Override
	public boolean existsByUserIdAndBoardId(Long userIdx, Long boardId) {
		//예외) 일단 사용자가 존재하는 사용자인지 , 게시물은 존재하는지 각각 확인해야 할듯
		User user = userRepository.findById(userIdx).orElseThrow();
		Board board = boardRepository.findById(boardId).orElseThrow();

		Optional<Likes> like = likesRepository.findByUserIdxAndBoardId(userIdx,boardId);

		if(like.isPresent()) {//사용자가 특정 게시물에 좋아요를 누른 상태라면
			return true;
		}

		return false;
	}

	@Transactional
	@Override
	public void addLike(Long userIdx, Long boardId) {
		//뭔가 save를 써야 하지 않을까..라는 생각이 먼저 듦
		//예외) 사용자와 게시물이 존재하는지 확인해야함, add했는데 사용자가 이미 좋아요를 누른 상태라면?
		User user = userRepository.findById(userIdx).orElseThrow();
		Board board = boardRepository.findById(boardId).orElseThrow();
		Optional<Likes> like = likesRepository.findByUserIdxAndBoardId(userIdx,boardId);

		//2.사용자가 이미 좋아요를 누른 상태인데 또 누르려고 한다면
		if(like.isPresent()) {
//			"좋아요를 이미 누른 사용자 입니다" 예외처리
		}

		Likes newLike = new Likes(user,board);
		likesRepository.save(newLike);

	}

	@Transactional
	@Override
	public void cancelLike(Long userIdx, Long boardId) {
		//delete를 쓰는건가?..
		User user = userRepository.findById(userIdx).orElseThrow();
		Board board = boardRepository.findById(boardId).orElseThrow();
		Optional<Likes> like = likesRepository.findByUserIdxAndBoardId(userIdx,boardId);

		//사용자가 좋아요를 누르지 않은 상태인데 좋아요 취소를 요청한다면?
		if(like.isEmpty()) {
//			"좋아요를 누르지 않아서 취소할 수 없습니다." 예외
		}

		likesRepository.delete(like.get());
	}

}
