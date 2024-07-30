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
import org.springframework.security.core.userdetails.UserDetails;
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

    public User getUserFromUserDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserException(UserResponseStatus.NOT_FOUND));
    }

    public MyPageResponse getMyPage(UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        return new MyPageResponse(user.getProfile(), user.getUsername());
    }

    public MyPageInfoResponse getMyPageInfo(UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        return new MyPageInfoResponse(user.getUsername(), user.getEmail());
    }

    @Transactional
    public void updateUsername(UserDetails userDetails, UpdateNameRequest updateNameRequest) {
        User user = getUserFromUserDetails(userDetails);

        if (!user.getUsername().equals(updateNameRequest.username())) {
            if (userRepository.existsByUsername(updateNameRequest.username())) {
                throw new UserException(UserResponseStatus.NAME_ALREADY_EXIST);
            }
        }
        try {
            user.updateUsername(updateNameRequest.username());
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserException(UserResponseStatus.DATABASE_INSERT_ERROR);
        }
    }

    @Transactional
    public void updateProfileImage(UserDetails userDetails, UpdateProfileImageRequest updateProfileImageRequest) {
        User user = getUserFromUserDetails(userDetails);

        try {
            user.updateProfile(updateProfileImageRequest.profile());
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserException(UserResponseStatus.DATABASE_INSERT_ERROR);
        }
    }
}
