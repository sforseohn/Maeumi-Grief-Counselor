package com.gcl.maeumi.counsel.service;

import com.gcl.maeumi.counsel.dto.CounselDto.DialogflowRequestDto;
import com.gcl.maeumi.counsel.entity.Counsel;
import com.gcl.maeumi.counsel.repository.CounselRepository;
import com.gcl.maeumi.member.entity.Member;
import com.gcl.maeumi.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class CounselService {
    @Autowired
    private CounselRepository counselRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public Optional<Counsel> processUserResponse(DialogflowRequestDto request) {
        // 세션 ID, 질문 ID, 사용자 응답 추출
        String sessionId = extractSessionId(request.getSession());
        Integer questionId = (Integer) request.getQueryResult().getParameters().get("questionId");
        String userResponse = (String) request.getQueryResult().getParameters().get("userResponse");
        long userId = (Integer) request.getQueryResult().getParameters().get("userId");
        Integer sessionNumber = (Integer) request.getQueryResult().getParameters().get("sessionNumber");

        Optional<Member> member = memberRepository.findById(userId);
        if (!member.isPresent()) {
            return Optional.empty();
        }

        // 이미 해당 세션 ID의 객체가 있다면 이어서 저장
        Optional<Counsel> existingCounsel = counselRepository.findBySessionId(sessionId);
        Counsel newCounsel;

        if (existingCounsel.isPresent()) {
            newCounsel = existingCounsel.get();

            // 사용자의 responses에 이어서 저장
            newCounsel.addResponse(questionId, userResponse);

        } else {
            newCounsel = Counsel.builder()
                    .member(member.get())
                    .sessionId(sessionId)
                    .sessionNumber(sessionNumber)
                    .startTime(new Date())
                    .build();
        }
        counselRepository.save(newCounsel);

        return Optional.of(newCounsel);
    }

    private String extractSessionId(String fullSession) {
        // URL에서 세션 ID 추출
        String sessionId = fullSession.substring(fullSession.lastIndexOf("/") + 1);
        System.out.println("full session is ... : " + fullSession + ", sessionId: " + sessionId);
        return sessionId;
    }
}
