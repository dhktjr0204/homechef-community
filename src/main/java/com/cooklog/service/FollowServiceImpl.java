package com.cooklog.service;

import com.cooklog.dto.FollowDTO;
import com.cooklog.dto.UserDTO;
import com.cooklog.exception.follow.AlreadyFollowingException;
import com.cooklog.exception.follow.AlreadyUnfollowedException;
import com.cooklog.exception.follow.SelfFollowNotAllowedException;
import com.cooklog.exception.follow.SelfUnfollowNotAllowedException;
import com.cooklog.exception.user.NotValidateUserException;
import com.cooklog.model.Follow;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;

import java.io.FileNotFoundException;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cooklog.repository.FollowRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

	//팔로우
	@Transactional
	@Override
	public void follow(User currentUser, Long followingUserIdx) {
		User followingUser = validateUser(followingUserIdx);

		if(currentUser.getIdx() == followingUser.getIdx()) {//자기 자신을 팔로우하면 안된다
			throw new SelfFollowNotAllowedException();
		}

		Optional<Follow> isExist = followRepository.findByFollowerIdAndFollowingId(currentUser.getIdx(), followingUser.getIdx());

		if(isExist.isPresent()) {//팔로우를 눌렀는데 이미 팔로우 관계라면?
			throw new AlreadyFollowingException();
		}

		Follow follow = new Follow(currentUser,followingUser);
		followRepository.save(follow);
	}

	//언팔로우
	@Transactional
	@Override
	public void unfollow(User currentUser, Long unfollowingUserIdx) {
		User unfollowingUser = validateUser(unfollowingUserIdx);

		if(currentUser.getIdx() == unfollowingUser.getIdx()) {//자기 자신을 언팔로우하면 안된다
			throw new SelfUnfollowNotAllowedException();
		}

		Optional<Follow> isExist = followRepository.findByFollowerIdAndFollowingId(currentUser.getIdx(),unfollowingUser.getIdx());

		if(isExist.isEmpty()) {//언팔로우를 눌렀는데 이미 언팔로우 관계라면?
			throw new AlreadyUnfollowedException();
		}

		followRepository.deleteByFollowerIdAndFollowingId(currentUser.getIdx(), unfollowingUser.getIdx());
	}

	//targetUser의 followingList를 찾는다
	@Override
	public Page<FollowDTO> getFollowingListByUserIdx(Long targetUserIdx, Pageable pageable) {
		User targetUser = validateUser(targetUserIdx);

		Page<Follow> followPage = followRepository.findByFollowerId(targetUser.getIdx(),pageable);

		return followPage.map(FollowDTO::new);
	}

	//targetUser의 followingList를 찾는데, currentUser가 이미 팔로우 한 사람들의 정보도 포함
	@Override
	public Page<FollowDTO> getFollowingListWithFollowStatus(Long targetUserIdx, UserDTO currentUser,Pageable pageable) {
		User targetUser = validateUser(targetUserIdx);

		Page<FollowDTO> followingList = followRepository.findFollowingListByUserIdxWithFollowStatus(targetUser.getIdx(), currentUser.getIdx(), pageable);

		//profileUrl을 넣어주는 작업
		for(FollowDTO followDTO : followingList) {
			String profileUrl = null;

			profileUrl = imageService.fileLoad(followDTO.getFollowingUserProfileImage());


        if (followingList.isEmpty()) {
            return Page.empty();
        }

        return followingList;
    }
  
	//targetUser의 followerList를 찾는데, currentUser가 이미 팔로우 한 사람들의 정보도 포함
	@Override
	public Page<FollowDTO> getFollowerListWithFollowStatus(Long targetUserIdx,UserDTO currentUser, Pageable pageable) {
		User targetUser = validateUser(targetUserIdx);

		Page<FollowDTO> followerList = followRepository.findFollowerListByUserIdxWithFollowStatus(targetUser.getIdx(), currentUser.getIdx(), pageable);

		//profileUrl을 넣어주는 작업
		for(FollowDTO followDTO : followerList) {
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
