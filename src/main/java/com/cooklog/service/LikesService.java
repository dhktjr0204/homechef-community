package com.cooklog.service;

public interface LikesService {

    Long getNumberOfLikesByBoardId(Long boardId);

    boolean existsByUserIdAndBoardId(Long userIdx,Long boardId);

    void addLike(Long userIdx,Long boardId);

    void cancelLike(Long userIdx,Long boardId);
}