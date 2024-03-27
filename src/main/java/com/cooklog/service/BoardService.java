package com.cooklog.service;

import com.cooklog.model.Board;

public interface BoardService {
	Board save(Long userId, String content);
}
