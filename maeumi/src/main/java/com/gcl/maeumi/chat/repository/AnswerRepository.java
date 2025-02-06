package com.gcl.maeumi.chat.repository;

import com.gcl.maeumi.chat.entity.Answer;
import com.gcl.maeumi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByUser(User user);

    // scenarioNum, questionNum을 기반으로 질문 찾기
    Optional<Answer> findByScenarioNumAndQuestionNum(Integer scenarioNum, Integer questionNum);

    Optional<Answer> findBySessionId(String sessionId);
}
