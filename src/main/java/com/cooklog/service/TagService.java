package com.cooklog.service;

import com.cooklog.model.Board;
import com.cooklog.model.Tag;

import java.util.List;

public interface TagService {
	List<Tag> save(List<String> tags, Board board);
	List<String> getTags(String tags);
}

