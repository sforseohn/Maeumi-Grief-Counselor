package com.gcl.maeumi.chat.dto;

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
        private Boolean isLastQuestion;
        private Integer nextQuestion;
        private String questionText;
        private AnswerType answerType;
        private List<Map<String, Object>> options;


        public static ChatResponseDto from(Question question) {
            return ChatResponseDto.builder()
                    .scenarioNum(question.getScenarioNum())
                    .nextQuestion(question.getQuestionNum())
                    .questionText(question.getQuestionText())
                    .answerType(question.getAnswerType())
                    .options(question.getOptions())
                    .isLastQuestion(false)
                    .build();
        }

        public static ChatResponseDto endSession() {
            return ChatResponseDto.builder()
                    .questionText(null)
                    .answerType(null)
                    .options(null)
                    .isLastQuestion(true)
                    .build();
        }
    }
}
