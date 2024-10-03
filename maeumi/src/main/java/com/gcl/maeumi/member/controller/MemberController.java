package com.gcl.maeumi.member.controller;

import com.gcl.maeumi.member.dto.MemberDto;
import com.gcl.maeumi.member.entity.Member;
import com.gcl.maeumi.member.service.MemberService;
import com.gcl.maeumi.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody MemberDto.SignupRequestDto request) {
        Optional<Member> member = memberService.join(request);
        if (member.isPresent()) {
            return ApiResponse.createSuccess(member.get().toString(), "회원가입에 성공했습니다.");
        } else {
            return ApiResponse.createFail("회원가입에 실패했습니다.");
        }
    }
}