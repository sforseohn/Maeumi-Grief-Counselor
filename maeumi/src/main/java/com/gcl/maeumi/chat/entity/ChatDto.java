package com.gcl.maeumi.chat.entity;

import com.gcl.maeumi.chat.AnswerType;
import com.gcl.maeumi.question.entity.Question;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public class ChatDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequestDto {
        @NotNull
        private Long userId;

        @NotNull
        private Integer scenarioNum;

        @NotBlank
        private String sessionId;

        @NotNull
        private Integer curQuestion;

        @NotNull
        private String userResponse;
    }

    @Data
    @Builder
    public static class ChatResponseDto {
        private Integer scenarioNum;
        private Integer nextQuestion;
        private String questionText;
        private AnswerType answerType;
        private List<Map<String, Object>> options;


        public static ChatResponseDto from(Question nextQuestion) {
            return ChatResponseDto.builder()
                    .scenarioNum(nextQuestion.getScenarioNum())
                    .nextQuestion(nextQuestion.getQuestionNum())
                    .questionText(nextQuestion.getQuestionText())
                    .answerType(nextQuestion.getAnswerType())
                    .options(nextQuestion.getOptions())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpenAIDto {
        @NotBlank
        private String text;

        public static OpenAIDto from(String text) {
            return OpenAIDto.builder()
                    .text(text)
                    .build();
        }
    }
}
