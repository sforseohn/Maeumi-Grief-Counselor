package com.gcl.maeumi.chat.service;

import com.gcl.maeumi.chat.dto.OpenAIDto.*;
import com.gcl.maeumi.common.error.BusinessException;
import com.gcl.maeumi.common.error.ErrorCode;
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
    private static final String MODEL_NAME = "gpt-4o-mini";

    @Value("${openai.api-key}")
    private String apiKey;
    private final OkHttpClient client;

    public OpenAIService() {
        this.client = new OkHttpClient.Builder()
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
    }

    // 사용자의 응답을 바탕으로 감정 분석 수행
    public OpenAIResponseDto analyzeEmotion(OpenAIRequestDto request) {
        String userResponse = request.getUserText();
        String systemQuestion = request.getSystemText();
        JSONObject requestBody = buildRequestMessage(systemQuestion, userResponse);
        String responseJson = sendRequestToOpenAI(requestBody);

        return OpenAIResponseDto.from(extractResponse(responseJson));
    }

    // OpenAI API에 보낼 Request message 생성
    private JSONObject buildRequestMessage(String systemQuestion, String userResponse) {
        return new JSONObject()
                .put("model", MODEL_NAME)
                .put("messages", new JSONArray()
                    .put(new JSONObject().put("role", "system").put("content", getSystemMessage(systemQuestion)))
                    .put(new JSONObject().put("role", "user").put("content", userResponse))
                );
    }

    private String getSystemMessage(String systemQuestion) {
        return String.format("""
                당신은 애도 상담사 챗봇입니다. 사용자는 애도와 관련된 감정적이고 민감한 이야기를 나누기 위해 당신을 찾았습니다. 사용자의 답변을 바탕으로 감정 상태를 분석하고 공감해주세요.
                현재 당신이 사용자에게 보낸 질문은 "%s"입니다. 
                
                - 모든 답변은 한글 줄글 1~2문장으로 하세요.
                - 응답은 진심 어린 위로와 공감을 전달하되, 해결책을 제안하기보다는 감정과 경험을 받아들이는 형태로만 작성하세요.
                - 자연스럽고 인간적인 존댓말로 답변을 작성하되, 이모티콘이나 불필요한 꾸밈은 사용하지 마세요.
                - 예를 들어, "친구들에게서 이해받지 못한다는 깊은 외로움과 슬픔을 느끼셨군요. 혼자서도 정말 많이 애쓰셨을 텐데, 그 마음이 고스란히 전해져요."처럼 사용자의 감정을 인정하는 데만 집중하세요.
                """, systemQuestion);
    }

    // OpenAI API에 요청 전송 및 응답 반환
    private String sendRequestToOpenAI(JSONObject requestBody) {
        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new BusinessException(ErrorCode.OPENAI_RESPONSE_INVALID);
            }

            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                handleErrorResponse(response.code(), response.message(), responseBody);
            }
            return responseBody;
        } catch (IOException e) {
            logger.error("OpenAI API 요청 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPENAI_REQUEST_FAILED);
        }
    }

    // OpenAI 오류 응답 처리
    private void handleErrorResponse(int statusCode, String message, String responseBody) {
        ErrorCode errorCode = mapOpenAIError(responseBody);
        logger.error("OpenAI API 요청 실패: HTTP {} - {}, 오류 코드: {}", statusCode, message, errorCode);
        throw new BusinessException(errorCode);
    }

    // OpenAI 오류 메시지로부터 오류 코드 반환
    private ErrorCode mapOpenAIError(String responseBody) {
        String errorMessage = parseErrorMessage(responseBody);

        if (errorMessage == null) {
            return ErrorCode.OPENAI_REQUEST_FAILED;
        }

        if (errorMessage.contains("invalid_api_key")) {
            return ErrorCode.OPENAI_INVALID_API_KEY;
        }

        if (errorMessage.contains("quota")) {
            return ErrorCode.OPENAI_QUOTA_EXCEEDED;
        }

        return ErrorCode.OPENAI_REQUEST_FAILED;
    }

    // OpenAI API 응답에서 오류 메시지 추출
    private String parseErrorMessage(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.optJSONObject("error") != null ?
                    jsonResponse.getJSONObject("error").optString("message", null) :
                    null;
        } catch (Exception e) {
            logger.warn("OpenAI 응답에서 오류 메시지를 파싱할 수 없음: {}", responseBody);
            return null;
        }
    }

    // OpenAI API 응답에서 감정 분석 결과 추출
    private String extractResponse(String responseJson) {
        JSONObject response = new JSONObject(responseJson);
        JSONArray choices = response.optJSONArray("choices");

        if (choices == null || choices.isEmpty()) {
            throw new BusinessException(ErrorCode.OPENAI_RESPONSE_INVALID);
        }

        String content = choices.getJSONObject(0)
                .optJSONObject("message")
                .optString("content", "")
                .trim();

        if (content.isEmpty()) {
            throw new BusinessException(ErrorCode.OPENAI_RESPONSE_INVALID);
        }

        return content;
    }
}