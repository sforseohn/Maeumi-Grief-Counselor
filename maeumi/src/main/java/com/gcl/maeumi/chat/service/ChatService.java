package com.gcl.maeumi.chat.service;

import com.gcl.maeumi.chat.AnswerType;
import com.gcl.maeumi.chat.dto.SessionDto.*;
import com.gcl.maeumi.chat.entity.Answer;
import com.gcl.maeumi.chat.dto.ChatDto.*;
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
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    // 사용자 응답 처리
    public ChatResponseDto handleUserResponse(ChatRequestDto request) {
        // 사용자 조회
        User user = findUser(request.getUserId());

        // 현재 질문을 바탕으로 다음 질문 결정
        Question curQuestion = findQuestion(request.getScenarioNum(), request.getCurQuestion());
        int nextQuestionNum = findNextQuestionNumber(curQuestion, request.getSessionId(), request.getUserResponse());

        if (nextQuestionNum == -1) {
            saveUserAnswer(request, user);
            return ChatResponseDto.endSession();
        }

        Question nextQuestion = findQuestion(request.getScenarioNum(), nextQuestionNum);

        // 사용자 응답 저장
        saveUserAnswer(request, user);

        return ChatResponseDto.from(nextQuestion);
    }

    // 세션 초기 설정
    public SessionResponseDto initiateSession(SessionRequestDto request) {
        // 사용자 조회
        User user = findUser(request.getUserId());

        Optional<Answer> latestAnswer = findUserAnswer(request.getUserId(), request.getScenarioNum());
        String sessionId = "";
        int curQuestionNum;

        /*
        // 해당 시나리오의 사용자 기록이 있으면 가져오기
        if (latestAnswer.isPresent()) {
            sessionId = latestAnswer.get().getSessionId();
            Question latestQuestion = findQuestion(request.getScenarioNum(), latestAnswer.get().getQuestionNum());

            if (latestQuestion.getNextQuestion() != -1) {
                curQuestionNum = findNextQuestionNumber(latestQuestion, latestAnswer.get().getSessionId(), latestAnswer.get().getUserResponse());
            }
            else {
                sessionId = UUID.randomUUID().toString();
                curQuestionNum = 1;
            }
        }
        else { // 존재하지 않으면 새로운 세션 ID 생성
            sessionId = UUID.randomUUID().toString();
            curQuestionNum = 1;
        }*/

        sessionId = UUID.randomUUID().toString();
        curQuestionNum = 1;

        Question curQuestion = findQuestion(request.getScenarioNum(), curQuestionNum);

        return SessionResponseDto.from(sessionId, curQuestion);
    }

    private Optional<Answer> findUserAnswer(Long userId, Integer scenarioNum) {
        return answerRepository.findTopByUserIdAndScenarioNumOrderByCreatedTimeDesc(userId, scenarioNum);
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

    private int findNextQuestionNumber(Question question, String sessionId, String answerText) {
        if (question.getQuestionNum() == 28) {
            return figureGriefStyle(question, sessionId, answerText);
        }

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

        if (selectedIndex < 0 || selectedIndex > options.size()) {
            log.error("객관식 답변 인덱스 범위 초과: answerText={}, 선택지 개수={}", answerText, options.size());
            throw new BusinessException(ErrorCode.INVALID_USER_ANSWER);
        }

        Map<String, Object> selectedOption = question.getOptions().get(selectedIndex);

        if (selectedOption.containsKey("nextQuestion")) {
            return (Integer) selectedOption.get("nextQuestion");
        }
        return question.getQuestionNum() + 1;
    }

    // 애도 스타일 결정
    private int figureGriefStyle(Question question, String sessionId, String answerText) {
        Optional<Answer> prevAnswer = answerRepository.findBySessionIdAndQuestionNum(sessionId, question.getQuestionNum() - 1);

        if (!prevAnswer.isPresent()) {
            log.error("27번 답변을 조회할 수 없음");
            throw new BusinessException(ErrorCode.ANSWER_NOT_FOUND);
        }

        final String YES = "0";
        final String NO = "1";
        String prevSelection = prevAnswer.get().getUserResponse();

        if (prevSelection.equals(NO) && answerText.equals(NO)) {
            return 29;
        }

        if (prevSelection.equals(YES) && answerText.equals(YES)) {
            return 30;
        }

        if (prevSelection.equals(YES) && answerText.equals(NO)) {
            return 31;
        }

        return 32;
    }
}

