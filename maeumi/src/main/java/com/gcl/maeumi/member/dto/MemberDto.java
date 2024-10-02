package com.gcl.maeumi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    @Data
    @AllArgsConstructor
    public static class SignupRequestDto{
        public String username;
        public String name;
        public String password;
    }

    @AllArgsConstructor
    @Data
    public static class SignupResponseDto {
        Long userId;
    }
}