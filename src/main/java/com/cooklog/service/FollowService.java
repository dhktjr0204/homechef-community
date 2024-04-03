package com.cooklog.service;

import com.cooklog.dto.FollowDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    void follow(Long followerId,Long followingId);

    void unfollow(Long followerId,Long followingId);

    Page<FollowDTO> findFollowingListByUserIdx(Long userIdx, Pageable pageable);

    Page<FollowDTO> findFollowerListByUserIdx(Long userIdx, Pageable pageable);

    Page<FollowDTO> getFollowingListWithFollowStatus(Long userIdx, Long currentUserIdx, Pageable pageable);

    Page<FollowDTO> getFollowerListWithFollowStatus(Long userIdx, Long currentUserIdx, Pageable pageable);
}

