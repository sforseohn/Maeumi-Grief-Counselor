package com.gcl.maeumi.answer.service;

import com.gcl.maeumi.answer.entity.Answer;
import com.gcl.maeumi.answer.repository.AnswerRepository;
import com.gcl.maeumi.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    // 특정 사용자의 답변 조회
    public Optional<Answer> getAnswerByUser(User user) {
        return answerRepository.findByUser(user);
    }

    // 특정 scenarioNum과 questionNum을 가진 답변 조회
    public Optional<Answer> getAnswerByScenarioAndQuestion(Integer scenarioNum, Integer questionNum) {
        return answerRepository.findByScenarioNumAndQuestionNum(scenarioNum, questionNum);
    }

    // 특정 세션 ID로 답변 조회
    public Optional<Answer> getAnswerBySessionId(String sessionId) {
        return answerRepository.findBySessionId(sessionId);
    }
}

