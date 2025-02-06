package com.gcl.maeumi.chat.entity;
import com.gcl.maeumi.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`answers`")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 사용자 ID

    private Integer scenarioNum; // 회차 번호

    private Integer questionNum; // 질문 번호

    private String sessionId; // 세션 ID

    private String answerText; // 사용자 응답 데이터

    private LocalDateTime createdTime = LocalDateTime.now();

    public static Answer from(ChatDto.ChatRequestDto request, User user) {
        return Answer.builder()
                .user(user)
                .scenarioNum(request.getScenarioNum())
                .questionNum(request.getCurQuestion())
                .sessionId(request.getSessionId())
                .answerText(request.getUserResponse())
                .build();
    }
}

