package com.cooklog.service;

import com.cooklog.dto.FollowDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    void follow(User currentUser,Long followingUserIdx);

    void unfollow(User currentUser,Long unfollowingUserIdx);

    Page<FollowDTO> getFollowingListByUserIdx(Long targetUserIdx, Pageable pageable);

    Page<FollowDTO> getFollowerListByUserIdx(Long targetUserIdx, Pageable pageable);

    Page<FollowDTO> getFollowingListWithFollowStatus(Long targetUserIdx, UserDTO currentUser, Pageable pageable);

    Page<FollowDTO> getFollowerListWithFollowStatus(Long targetUserIdx, UserDTO currentUser, Pageable pageable);
}

