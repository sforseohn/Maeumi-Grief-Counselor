package com.gcl.maeumi.chat.dto;

import com.gcl.maeumi.chat.AnswerType;
import com.gcl.maeumi.question.entity.Question;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class SessionDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionRequestDto {
        @NotNull
        private Long userId;

        @NotNull
        private Integer scenarioNum;
    }

    @Data
    @Builder
    public static class SessionResponseDto {

        private String sessionId;
        private Integer scenarioNum;
        private Integer nextQuestion;
        private String questionText;
        private AnswerType answerType;
        private List<Map<String, Object>> options;

        public static SessionResponseDto from(String sessionId, Question question) {
            return SessionResponseDto.builder()
                    .sessionId(sessionId)
                    .scenarioNum(question.getScenarioNum())
                    .nextQuestion(question.getQuestionNum())
                    .questionText(question.getQuestionText())
                    .answerType(question.getAnswerType())
                    .options(question.getOptions())
                    .build();
        }
    }
}
