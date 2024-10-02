package com.gcl.maeumi.member.controller;

import com.gcl.maeumi.member.dto.MemberDto;
import com.gcl.maeumi.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public String signup(@RequestBody MemberDto.SignupRequestDto request) {
        if (memberService.join(request)) {
            return request.toString();
        }
        else {
            return "회원가입에 실패했습니다.";
        }
    }
}