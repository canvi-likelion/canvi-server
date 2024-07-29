package com.canvi.hama.domain.user.controller;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponse;
import com.canvi.hama.domain.user.dto.MyPageInfoResponse;
import com.canvi.hama.domain.user.dto.MyPageResponse;
import com.canvi.hama.domain.user.dto.UpdateNameRequest;
import com.canvi.hama.domain.user.dto.UpdateProfileImageRequest;
import com.canvi.hama.domain.user.service.UserService;
import com.canvi.hama.domain.user.swagger.MyPageGetApi;
import com.canvi.hama.domain.user.swagger.MyPageInfoGetApi;
import com.canvi.hama.domain.user.swagger.NameUpdateApi;
import com.canvi.hama.domain.user.swagger.ProfileImageUpdateApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @MyPageGetApi
    @GetMapping("/{userId}")
    public BaseResponse<MyPageResponse> getMyPage(@PathVariable("userId") Long userId) {
        try {
            return new BaseResponse<>(userService.getMyPage(userId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @MyPageInfoGetApi
    @GetMapping("/info/{userId}")
    public BaseResponse<MyPageInfoResponse> getMyPageInfo(@PathVariable("userId") Long userId) {
        try {
            return new BaseResponse<>(userService.getMyPageInfo(userId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @NameUpdateApi
    @PatchMapping("/name")
    public BaseResponse<String> updateUserName(@RequestBody UpdateNameRequest updateNameRequest) {
        try {
            userService.updateName(updateNameRequest);
            return new BaseResponse<>("이름 수정을 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ProfileImageUpdateApi
    @PatchMapping("/profile-image")
    public BaseResponse<String> updateProfile(@RequestBody UpdateProfileImageRequest updateProfileImageRequest) {
        try {
            userService.updateProfileImage(updateProfileImageRequest);
            return new BaseResponse<>("프로필 이미지 수정을 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
