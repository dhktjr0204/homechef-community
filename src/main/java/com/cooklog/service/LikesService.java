package com.cooklog.service;

import com.cooklog.model.User;

public interface LikesService {

    Long getNumberOfLikesByBoardId(Long boardId);

    void addLike(User currentUser,Long boardId);

    void cancelLike(User currentUser,Long boardId);
}