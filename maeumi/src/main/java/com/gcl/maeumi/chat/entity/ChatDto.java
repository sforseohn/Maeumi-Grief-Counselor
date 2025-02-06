package com.gcl.maeumi.chat.entity;

import com.gcl.maeumi.chat.AnswerType;
import com.gcl.maeumi.question.entity.Question;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
public class ChatDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequestDto {
        private Long userId;
        private Integer scenarioNum;
        private String sessionId;
        private Integer curQuestion;
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
}
