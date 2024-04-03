package com.cooklog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdateRequestDTO {
    private Long userId;
    private String content;
    private List<String> tags;
    //기존에 있는 이미지들
    private List<String> imageUrls;
}
