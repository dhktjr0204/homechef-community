package com.cooklog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MyPageController {
    @GetMapping("")
    public String getMyPage(){
        return "myPage/myPage";
    }

    @GetMapping("/follower")
    public String getFollower(){
        return "myPage/followerPage";
    }

    @GetMapping("/edit")
    public String getProfileEditForm(){
        return "myPage/profileEditForm";
    }
}
