package com.canvi.hama.domain.user.controller;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @MyPageGetApi
    @GetMapping("/mypage")
    public BaseResponse<MyPageResponse> getMyPage(@AuthenticationPrincipal UserDetails userDetails) {
        return new BaseResponse<>(userService.getMyPage(userDetails));
    }

    @MyPageInfoGetApi
    @GetMapping("/info")
    public BaseResponse<MyPageInfoResponse> getMyPageInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return new BaseResponse<>(userService.getMyPageInfo(userDetails));
    }

    @NameUpdateApi
    @PatchMapping("/name")
    public BaseResponse<String> updateUsername(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid UpdateNameRequest updateNameRequest) {
        userService.updateUsername(userDetails, updateNameRequest);
        return new BaseResponse<>("이름 수정을 성공하였습니다.");
    }

    @ProfileImageUpdateApi
    @PatchMapping("/profile-image")
    public BaseResponse<String> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateProfileImageRequest updateProfileImageRequest) {
        userService.updateProfileImage(userDetails, updateProfileImageRequest);
        return new BaseResponse<>("프로필 이미지 수정을 성공하였습니다.");
    }
}
