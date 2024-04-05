package com.cooklog.service;

import com.cooklog.model.User;

public interface BookmarkService {

    void addMark(User currentUser,Long boardId);

    void cancelMark(User currentUser,Long boardId);

}
