package com.gcl.maeumi.chat.service;

import com.gcl.maeumi.chat.AnswerType;
import com.gcl.maeumi.chat.entity.Answer;
import com.gcl.maeumi.chat.entity.ChatDto.*;
import com.gcl.maeumi.chat.repository.AnswerRepository;
import com.gcl.maeumi.common.EntityFinder;
import com.gcl.maeumi.common.error.BusinessException;
import com.gcl.maeumi.common.error.ErrorCode;
import com.gcl.maeumi.question.entity.Question;
import com.gcl.maeumi.question.repository.QuestionRepository;
import com.gcl.maeumi.user.entity.User;
import com.gcl.maeumi.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public ChatResponseDto handleUserResponse(ChatRequestDto request) {
        // 사용자 조회
        User user = findUser(request.getUserId());

        // 현재 질문을 바탕으로 다음 질문 결정
        Question curQuestion = findQuestion(request.getScenarioNum(), request.getCurQuestion());
        int nextQuestionNum = determineNextQuestion(curQuestion, request.getUserResponse());
        Question nextQuestion = findQuestion(request.getScenarioNum(), nextQuestionNum);

        // 사용자 응답 저장
        saveUserAnswer(request, user);

        return ChatResponseDto.from(nextQuestion);
    }

    @Transactional
    protected void saveUserAnswer(ChatRequestDto request, User user) {
        answerRepository.save(Answer.from(request, user));
    }

    private User findUser(Long userId) {
        return EntityFinder.findByIdOrThrow(
                userRepository, userId,
                ErrorCode.USER_NOT_FOUND);
    }

    private Question findQuestion(Integer scenarioNum, Integer questionNum) {
        return EntityFinder.findByQueryOrThrow(
                questionRepository.findByScenarioNumAndQuestionNum(scenarioNum, questionNum),
                ErrorCode.QUESTION_NOT_FOUND
        );
    }

    private int determineNextQuestion(Question question, String answerText) {
        // next question 필드에 값이 있으면 반환
        if (question.getNextQuestion() != 0) {
            return question.getNextQuestion();
        }

        // 객관식 질문의 경우 선택지의 nextQuestion 필드가 있으면 반환
        if (question.getAnswerType() == AnswerType.MULTIPLE_CHOICE) {
            return getNextQuestionFromOptions(question, answerText);
        }

        // 없으면 현재 질문의 다음 질문 반환
        return question.getQuestionNum() + 1;
    }

    private int getNextQuestionFromOptions(Question question, String answerText) {
        if (!NumberUtils.isCreatable(answerText)) {
            log.error("객관식 답변 변환 실패: answerText={}", answerText);
            throw new BusinessException(ErrorCode.INVALID_USER_ANSWER);
        }

        int selectedIndex = Integer.parseInt(answerText);
        List<Map<String, Object>> options = question.getOptions();

        if (selectedIndex < 1 || selectedIndex > options.size()) {
            log.error("객관식 답변 인덱스 범위 초과: answerText={}, 선택지 개수={}", answerText, options.size());
            throw new BusinessException(ErrorCode.INVALID_USER_ANSWER);
        }

        Map<String, Object> selectedOption = question.getOptions().get(selectedIndex - 1);

        if (selectedOption.containsKey("nextQuestion")) {
            return (Integer) selectedOption.get("nextQuestion");
        }
        return question.getQuestionNum() + 1;
    }
}

