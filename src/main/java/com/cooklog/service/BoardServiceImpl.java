package com.cooklog.service;

import com.cooklog.dto.BoardCreateRequestDTO;
import com.cooklog.dto.BoardDTO;
import com.cooklog.dto.BoardUpdateRequestDTO;
import com.cooklog.exception.board.BoardNotFoundException;
import com.cooklog.exception.board.NoImageException;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Board;
import com.cooklog.model.Image;
import com.cooklog.model.Tag;
import com.cooklog.model.User;
import com.cooklog.repository.BoardRepository;
import com.cooklog.repository.TagRepository;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final ImageService imageService;

    @Override
    public Page<BoardDTO> getAllBoard(Pageable pageable, Long userId, Long lastBoardId, String sortType) {
        Page<Board> boardPage;

        if (lastBoardId == 0) {
            boardPage = boardRepository.findAll(pageable);
        } else if (sortType.equals("readCount: DESC")) {
            boardPage = boardRepository.findAllOrderByReadCount(lastBoardId, pageable);
        } else if (sortType.equals("createdAt: DESC")) {
            boardPage = boardRepository.findAllOrderByCreatedAt(lastBoardId, pageable);
        } else {
            boardPage = boardRepository.findAllOrderByLikesCount(lastBoardId, pageable);
        }

        if (boardPage.isEmpty()) {
            return Page.empty();
        }

        return boardPage.map(board -> convertBoardToDTO(board, userId));
    }

    @Override
    public Page<BoardDTO> getAllBoardWithFollow(Pageable pageable, Long userId, Long lastBoardId) {
        Page<Board> boardPage = boardRepository.findAllBoardWithFollow(userId, lastBoardId, pageable);

        if (boardPage.isEmpty()) {
            return Page.empty();
        }

        return boardPage.map(board -> convertBoardToDTO(board, userId));
    }

    @Override
    public BoardDTO getBoard(Long boardId, Long userId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        return convertBoardToDTO(board, userId);
    }

    @Override
    public Page<BoardDTO> getSearchByText(String keyword, Long userId, Long lastBoardId, Pageable pageable) {
        Page<Board> boardPage;

        if (lastBoardId == 0) {
            boardPage = boardRepository.findByContentContaining(keyword, pageable)
                    .orElse(Page.empty());
            ;
        } else {
            boardPage = boardRepository.findByContentContainingAndIdLessThanEqual(keyword, lastBoardId, pageable)
                    .orElse(Page.empty());
        }


        if (boardPage.isEmpty()) {
            return Page.empty();
        }

        return boardPage.map(board -> convertBoardToDTO(board, userId));
    }

    @Override
    public Page<BoardDTO> findBoardsByTags(String tags, Long userId, Long lastBoardId, Pageable pageable) {
        //받은 태그가 하나도 없을때 빈 페이지 리턴
        if (tags.isEmpty()) {
            return Page.empty();
        }

        List<String> tagList = Arrays.asList(tags.split(","));

        Page<Board> boardPage;

        //첫 요청일 때
        if (lastBoardId == 0) {
            boardPage=boardRepository.findBoardsByTagNames(tagList,pageable)
                    .orElse(Page.empty());
        } else {
            //두 번째 요청일 때부턴 첫 요청한 시점에 존재한 board들만 가져온다.
            boardPage = boardRepository.findBoardsByTagNamesWithLastBoardId(tagList, lastBoardId, pageable)
                    .orElse(Page.empty());
        }

        //검색한 결과가 하나도 없다면 빈 페이지 리턴
        if (boardPage.isEmpty()) {
            return Page.empty();
        }

        return boardPage.map(board -> convertBoardToDTO(board, userId));
    }

    private BoardDTO convertBoardToDTO(Board board, Long userId) {

        //게시글에 연결된 사진들 가져오기
        List<String> fileUrls = imageService.fileListLoad(board.getImages().stream()
                .map(Image::getName)
                .collect(Collectors.toList()));


        //유저 프로필 사진 가져오기
        String profileUrl = imageService.fileLoad(board.getUser().getProfileImage());

        BoardDTO boardDTO = BoardDTO.builder()
                .id(board.getId())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .readCount(board.getReadCount())
                .userId(board.getUser().getIdx())
                .userNickname(board.getUser().getNickname())
                .profileImageName(board.getUser().getProfileImage())
                .profileImageUrl(profileUrl)
                .userIsDelete(board.getUser().isDeleted())
                .imageNames(board.getImages().stream().map(Image::getName).collect(Collectors.toList()))
                .imageUrls(fileUrls)
                .tags(board.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .likeCount(board.getLikes().size())
                .isLike(board.getLikes().stream().anyMatch(like -> like.getUser().getIdx().equals(userId)))
                .isMarked(board.getBookmarks().stream().anyMatch(marks -> marks.getUser().getIdx().equals(userId)))
                .build();


        return boardDTO;
    }


    @Transactional
    @Override
    public Board save(Long userId, BoardCreateRequestDTO requestDTO, List<MultipartFile> images) {
        User user = userRepository.findById(userId)
                .orElseThrow(NotValidateUserException::new);

        Board boardBuilder = Board.builder()
                .user(user)
                .content(requestDTO.getContent())
                .readCount(0).build();

        Board board = boardRepository.save(boardBuilder);

        tagService.save(requestDTO.getTags(), board);

        if (images == null || images.isEmpty()) {
            throw new NoImageException();
        }

        imageService.fileListWrite(images, board);

        return board;
    }

    @Transactional
    @Override
    public Board updateBoard(Long boardId, BoardUpdateRequestDTO boardDTO, List<String> originalFiles, List<MultipartFile> newFiles) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        board.update(boardDTO.getContent());

        //기존 태그 모두 삭제
        tagRepository.deleteByBoard_Id(boardId);

        //새 태그 저장
        if (boardDTO.getTags() != null) {
            for (String tag : boardDTO.getTags()) {
                tagRepository.save(Tag.builder().board(board).name(tag).build());
            }
        }

        //수정 시 이미지가 1장도 첨부 안돼있을 경우
        if (originalFiles == null && newFiles == null) {
            throw new NoImageException();
        }

        imageService.updateFileList(board, originalFiles, newFiles);

        return board;

    }

    @Transactional
    @Override
    public void updateReadCnt(Long boardId) {

        boardRepository.updateReadCnt(boardId);

    }

    @Transactional
    @Override
    public void deleteBoard(Long boardId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        for (Image image : board.getImages()) {
            imageService.deleteS3(image.getName());
        }

        boardRepository.delete(board);

    }

    @Override
    public List<BoardDTO> findAllBoards() {

        List<Board> boards = boardRepository.findAll();

        return boards.stream().map(board -> new BoardDTO(
                board.getId(),
                board.getContent(),
                board.getCreatedAt()
        )).collect(Collectors.toList());

    }

    @Override
    public List<BoardDTO> findBoardsByUserId(Long userId) {
        return boardRepository.findByUserIdx(userId).stream()
                .map(board -> new BoardDTO(board.getId(), board.getContent(), board.getCreatedAt()))
                .collect(Collectors.toList());
    }
}

