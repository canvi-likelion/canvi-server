package com.canvi.hama.domain.user.service;


import com.canvi.hama.domain.user.dto.MyPageInfoResponse;
import com.canvi.hama.domain.user.dto.MyPageResponse;
import com.canvi.hama.domain.user.dto.UpdateNameRequest;
import com.canvi.hama.domain.user.dto.UpdateProfileImageRequest;
import com.canvi.hama.domain.user.entity.User;
import com.canvi.hama.domain.user.exception.UserException;
import com.canvi.hama.domain.user.exception.UserResponseStatus;
import com.canvi.hama.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserResponseStatus.NOT_FOUND));
    }

    public MyPageResponse getMyPage(Long userId) {
        User user = getUserByUserId(userId);
        return new MyPageResponse(user);
    }

    public MyPageInfoResponse getMyPageInfo(Long userId) {
        User user = getUserByUserId(userId);
        return new MyPageInfoResponse(user);
    }

    @Transactional
    public void updateName(UpdateNameRequest updateNameRequest) {
        User user = getUserByUserId(updateNameRequest.getUserId());

        if (!user.getUsername().equals(updateNameRequest.getUsername())) {
            if (userRepository.existsByUsername(updateNameRequest.getUsername())) {
                throw new UserException(UserResponseStatus.NAME_ALREADY_EXIST);
            }
        }
        try {
            user.setUsername(updateNameRequest.getUsername());
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserException(UserResponseStatus.DATABASE_INSERT_ERROR);
        }
    }

    @Transactional
    public void updateProfileImage(UpdateProfileImageRequest updateProfileImageRequest) {
        User user = getUserByUserId(updateProfileImageRequest.getUserId());
        user.setProfile(updateProfileImageRequest.getProfile());
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserException(UserResponseStatus.DATABASE_INSERT_ERROR);
        }
    }
}
