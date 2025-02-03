package com.gcl.maeumi.user.controller;

import com.gcl.maeumi.user.dto.UserDto;
import com.gcl.maeumi.user.entity.User;
import com.gcl.maeumi.user.service.UserService;
import com.gcl.maeumi.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody UserDto.SignupRequestDto request) {
        Optional<User> user = userService.join(request);
        if (user.isPresent()) {
            return ApiResponse.createSuccess(user.get().toString(), "회원가입에 성공했습니다.");
        } else {
            return ApiResponse.createFail("회원가입에 실패했습니다.");
        }
    }
}