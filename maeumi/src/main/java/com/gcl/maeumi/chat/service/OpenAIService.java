package com.gcl.maeumi.chat.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class OpenAIService {
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Value("${openai.api-key}")
    private String apiKey;
    private final OkHttpClient client;

    public OpenAIService() {
        // Create OkHttpClient with a timeout of 300 seconds
        this.client = new OkHttpClient.Builder()
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
    }

    public String analyzeEmotion(String promptText, String testSentence) {
        try {
            // 프롬프트와 사용자 입력을 바탕으로 요청 생성
            JSONObject requestBody = createRequestBody(promptText, testSentence);
            // OpenAI API 요청 전송
            String responseJson = getOpenAIResponse(requestBody);
            // string 형식으로 감정 분석 결과 반환
            return extractContent(responseJson);
        } catch (Exception e) {
            logger.error("Error analyzing emotion: {}", e.getMessage(), e);
            return null;
        }
    }

    // OpenAI API 요청 생성
    private JSONObject createRequestBody(String promptText, String testSentence) {
        JSONArray messagesArray = new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", promptText))
                .put(new JSONObject().put("role", "user").put("content", testSentence));

        return new JSONObject()
                .put("model", "gpt-4o-mini")
                .put("messages", messagesArray);
    }

    // OpenAI API 요청을 보내고 응답 반환
    private String getOpenAIResponse(JSONObject requestBody) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                // OpenAI API에서 반환하는 에러 메시지 가져오기
                String errorMessage = "Unknown error";
                try {
                    JSONObject errorJson = new JSONObject(responseBody);
                    errorMessage = errorJson.has("error") ? errorJson.getJSONObject("error").getString("message") : response.message();
                } catch (Exception e) {
                    logger.error("Failed to parse error response: {}", e.getMessage(), e);
                }

                // 로그 출력 (HTTP 코드 + 상세 에러 메시지)
                logger.error("OpenAI API Request failed: HTTP {} - {} | Error: {}", response.code(), response.message(), errorMessage);
            }
            return responseBody;
        }
    }

    // OpenAI API 응답에서 감정 분석 결과 추출
    private String extractContent(String responseJson) {
        if (responseJson == null) {
            return null;
        }

        JSONObject responseObj = new JSONObject(responseJson);
        // OpenAI API의 정상 응답인지 확인
        if (responseObj.has("choices")) {
            return responseObj.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }

        // 오류 응답이 있을 경우 에러 메시지 반환
        if (responseObj.has("error")) {
            JSONObject errorObj = responseObj.getJSONObject("error");
            String errorMessage = errorObj.optString("message", "Unknown error");
            String errorCode = errorObj.optString("code", "Unknown code");

            logger.error("OpenAI API Error: Code: {}, Message: {}", errorCode, errorMessage);
            return "Error: " + errorMessage + " (Code: " + errorCode + ")";
        }

        return "Error: Unexpected response format.";
    }
}
