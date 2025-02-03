package com.gcl.maeumi.answer.entity;
import com.gcl.maeumi.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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
}

