package com.gcl.maeumi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    @Data
    @AllArgsConstructor
    public static class SignupRequestDto{
        public String username;
        public String name;
        public String password;
    }

    @Data
    @AllArgsConstructor
    public static class SignupResponseDto {
        Long userId;
    }
}