package com.gcl.maeumi.question.repository;

import com.gcl.maeumi.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // scenarioNum, questionNum을 기반으로 질문 찾기
    Optional<Question> findByScenarioNumAndQuestionNum(Integer scenarioNum, Integer questionNum);
}
