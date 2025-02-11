package com.gcl.maeumi.chat.repository;

import com.gcl.maeumi.chat.entity.Answer;
import com.gcl.maeumi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByUser(User user);

    Optional<Answer> findBySessionIdAndQuestionNum(String sessionId, int questionNum);

    // 특정 사용자의 특정 시나리오에서 가장 최근의 세션 ID 가져오기
    Optional<Answer> findTopByUserIdAndScenarioNumOrderByCreatedTimeDesc(Long userId, Integer scenarioNum);

}
