package com.gcl.maeumi.chat.controller;

import com.gcl.maeumi.chat.service.OpenAITesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/emotion-analysis")
public class EmotionAnalysisController {
    private final OpenAITesterService openAITesterService;

    @Autowired
    public EmotionAnalysisController(OpenAITesterService openAITesterService) {
        this.openAITesterService = openAITesterService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> analyzeSentiment(@RequestBody Map<String, String> payload) {
        String promptText = payload.get("promptText"); // 프롬프트
        String testSentence = payload.get("testSentence"); // 입력 문장
        Map<String, String> responseMap = new HashMap<>();

        try {
            String analysis = openAITesterService.analyzeEmotion(promptText, testSentence);
            responseMap.put("analysis", analysis);
        } catch (Exception e) {
            // 로깅 프레임워크를 사용한 예외 처리
            e.printStackTrace();
            responseMap.put("error", "An error occurred while analyzing sentiment: " + e.getMessage());
            return ResponseEntity.status(500).body(responseMap);
        }

        return ResponseEntity.ok(responseMap);
    }
}