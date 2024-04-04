package com.cooklog.service;

import com.cooklog.dto.FollowDTO;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Follow;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;

import java.io.FileNotFoundException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import com.cooklog.repository.FollowRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    @Transactional
    @Override
    public void follow(Long followerId, Long followingId) {
        User follower = validateUser(followerId);
        User following = validateUser(followingId);

        if (follower.getIdx() == following.getIdx()) {//자기 자신을 팔로우하면 안된다
            throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없습니다.");
        }

        Optional<Follow> isExist = followRepository.findByFollowerIdAndFollowingId(follower.getIdx(), following.getIdx());

        if (isExist.isPresent()) {//팔로우를 눌렀는데 이미 팔로우 관계라면?
            throw new IllegalArgumentException("이미 팔로우 중인 유저입니다.");
        }

        Follow follow = new Follow(follower, following);
        followRepository.save(follow);
    }

    @Transactional
    @Override
    public void unfollow(Long followerId, Long followingId) {
        User follower = validateUser(followerId);
        User following = validateUser(followingId);

        if (follower.getIdx() == following.getIdx()) {//자기 자신을 언팔로우하면 안된다
            throw new IllegalArgumentException("자기 자신을 언팔로우 할 수 없습니다.");
        }

        Optional<Follow> isExist = followRepository.findByFollowerIdAndFollowingId(follower.getIdx(), following.getIdx());

        if (isExist.isEmpty()) {//언팔로우를 눌렀는데 이미 언팔로우 관계라면?
            throw new IllegalArgumentException("이미 언팔로우 중인 유저입니다.");
        }

        followRepository.deleteByFollowerIdAndFollowingId(follower.getIdx(), following.getIdx());
    }

    @Override
    public Page<FollowDTO> findFollowingListByUserIdx(Long userIdx, Pageable pageable) {
        User user = validateUser(userIdx);

        Page<Follow> followPage = followRepository.findByFollowerId(userIdx, pageable);
        return followPage.map(FollowDTO::new);

    }

    @Override
    public Page<FollowDTO> findFollowerListByUserIdx(Long userIdx, Pageable pageable) {
        User user = validateUser(userIdx);

        Page<Follow> followingPage = followRepository.findByFollowingId(userIdx, pageable);

        if (followingPage.isEmpty()) {
            return Page.empty();
        }

        return followingPage.map(FollowDTO::new);
    }

    @Override
    public Page<FollowDTO> getFollowingListWithFollowStatus(Long userIdx, Long currentUserIdx, Pageable pageable) {
        User user = validateUser(userIdx);
        User currentUser = validateUser(currentUserIdx);

        Page<FollowDTO> followingList = followRepository.findFollowingListByUserIdxWithFollowStatus(userIdx, currentUserIdx, pageable);

        for (FollowDTO followDTO : followingList) {
            String profileUrl = null;

            profileUrl = imageService.fileLoad(followDTO.getFollowingUserProfileImage());

            followDTO.setFollowingUserProfileImage(profileUrl);
        }

        if (followingList.isEmpty()) {
            return Page.empty();
        }

        return followingList;
    }

    @Override
    public Page<FollowDTO> getFollowerListWithFollowStatus(Long userIdx, Long currentUserIdx, Pageable pageable) {
        User user = validateUser(userIdx);
        User currentUser = validateUser(currentUserIdx);

        Page<FollowDTO> followerList = followRepository.findFollowerListByUserIdxWithFollowStatus(userIdx, currentUserIdx, pageable);

        for (FollowDTO followDTO : followerList) {
            String profileUrl = null;

            profileUrl = imageService.fileLoad(followDTO.getFollowerUserProfileImage());


            followDTO.setFollowerUserProfileImage(profileUrl);
        }

        if (followerList.isEmpty()) {
            return Page.empty();
        }

        return followerList;
    }

    private User validateUser(Long userIdx) {
        return userRepository.findById(userIdx).orElseThrow(NotValidateUserException::new);
    }
}
