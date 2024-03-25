package com.cooklog.service;

import com.cooklog.model.Board;
import com.cooklog.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cooklog.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> save(String tags, Board board) {
        List<Tag> tagList=new ArrayList<>();

        if (!tags.isEmpty()) {
            String[] requestTags = tags.split(",");
            for (String tag : requestTags) {
                Tag saveTag = tagRepository.save(Tag.builder().board(board).name(tag).build());
                tagList.add(saveTag);
            }
        }

        return tagList;
    }

    //구현 로직
}
