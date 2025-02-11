package com.gcl.maeumi.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OpenAIDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OpenAIRequestDto {
        @NotNull
        private String systemText;

        @NotBlank
        private String userText;
    }

    @Getter
    @Builder
    public static class OpenAIResponseDto {
        private String text;

        public static OpenAIResponseDto from(String text) {
            return OpenAIResponseDto.builder()
                    .text(text)
                    .build();
        }
    }
}