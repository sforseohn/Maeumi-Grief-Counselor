package com.gcl.maeumi.chat.service;

import com.gcl.maeumi.chat.AnswerType;
import com.gcl.maeumi.chat.entity.Answer;
import com.gcl.maeumi.chat.entity.ChatDto.*;
import com.gcl.maeumi.chat.repository.AnswerRepository;
import com.gcl.maeumi.question.entity.Question;
import com.gcl.maeumi.question.repository.QuestionRepository;
import com.gcl.maeumi.user.entity.User;
import com.gcl.maeumi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public ChatResponseDto processUserResponse(ChatRequestDto request) {
        // 사용자 응답 저장
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Answer answer = Answer.from(request, user);
        answerRepository.save(answer);

        // 다음 질문 반환
        Optional<Question> curQuestion = questionRepository.findByScenarioNumAndQuestionNum(answer.getScenarioNum(), answer.getQuestionNum());

        if (curQuestion.isPresent()) {
            int nextQuestionNum = getNextQuestionNum(curQuestion.get(), answer.getAnswerText());
            Question nextQuestion = questionRepository.findByScenarioNumAndQuestionNum(request.getScenarioNum(), nextQuestionNum)
                    .orElseThrow(() -> new IllegalArgumentException("Next question not found"));

            return ChatResponseDto.from(nextQuestion);
        }
        else {
            throw(new IllegalArgumentException("curQuestion not found"));
        }
    }

    private Integer getNextQuestionNum(Question question, String answerText) {
        // next question 필드가 있으면 반환
        if (question.getNextQuestion() != 0) {
            return question.getNextQuestion();
        }

        // 객관식 질문의 경우 선택지의 nextQuestion 필드가 있으면 반환
        if (question.getAnswerType() == AnswerType.MULTIPLE_CHOICE) {
            int selectedIndex = Integer.parseInt(answerText);
            Map<String, Object> selectedOption = question.getOptions().get(selectedIndex - 1);

            if (selectedOption.containsKey("nextQuestion")) {
                return (Integer) selectedOption.get("nextQuestion");
            }
        }

        // 없으면 현재 질문의 다음 질문 반환
        return question.getQuestionNum() + 1;
    }
}

