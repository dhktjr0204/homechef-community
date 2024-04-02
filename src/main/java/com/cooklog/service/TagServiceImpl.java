package com.cooklog.service;

import com.cooklog.model.Board;
import com.cooklog.model.Tag;
import com.cooklog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> save(List<String> tags, Board board) {
        List<Tag> tagList=new ArrayList<>();

        if(tags!=null){
            for (String tag : tags) {
                Tag saveTag = tagRepository.save(Tag.builder().board(board).name(tag).build());
                tagList.add(saveTag);
            }
        }

        return tagList;

    }
}
