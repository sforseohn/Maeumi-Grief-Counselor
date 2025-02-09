package com.gcl.maeumi.chat.controller;

import com.gcl.maeumi.chat.entity.ChatDto.*;
import com.gcl.maeumi.chat.service.ChatService;
import com.gcl.maeumi.chat.service.OpenAIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final OpenAIService openAIService;

    @PostMapping("/response")
    public ResponseEntity<ChatResponseDto> handleUserResponse(@RequestBody @Valid ChatRequestDto request) {
        return ResponseEntity.ok(chatService.handleUserResponse(request));
    }

    @PostMapping("/descriptive")
    public ResponseEntity<OpenAIDto> handleDescriptiveResponse(@RequestBody @Valid OpenAIDto request) {
        return ResponseEntity.ok(openAIService.analyzeEmotion(request));
    }
}
