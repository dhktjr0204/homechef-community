package com.cooklog.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageDTO {
    private Long id;
    private String imageUrl; // Image url 목록

}
