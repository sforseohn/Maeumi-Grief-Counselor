package com.gcl.maeumi.question.entity;
import com.gcl.maeumi.chat.AnswerType;
import com.gcl.maeumi.chat.JsonConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "`questions`")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer scenarioNum; // 회차 번호

    private Integer questionNum; // 질문 번호

    private String questionText; // 질문 내용

    @Enumerated(EnumType.STRING)
    private AnswerType answerType; // 응답 유형

    private Integer nextQuestion; // 다음 질문 번호

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Map<String, Object>> options; // 선택지와 다음 질문 번호를 함께 저장
}

