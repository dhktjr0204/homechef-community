package com.cooklog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDTO {
    private String profileImage;
    private String nickname;
    private String introduction;
}
