package com.cooklog.service;

public interface BookmarkService {

    void addMark(Long userIdx,Long boardId);

    void cancelMark(Long userIdx,Long boardId);

}
