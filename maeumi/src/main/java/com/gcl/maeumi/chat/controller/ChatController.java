package com.gcl.maeumi.chat.controller;

import com.gcl.maeumi.chat.dto.ChatDto.*;
import com.gcl.maeumi.chat.dto.OpenAIDto.*;
import com.gcl.maeumi.chat.dto.SessionDto.*;
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

    @GetMapping("/session")
    public ResponseEntity<SessionResponseDto> initiateSession(@Valid @ModelAttribute SessionRequestDto request) {
        return ResponseEntity.ok(chatService.initiateSession(request));
    }

    @PostMapping("/response")
    public ResponseEntity<ChatResponseDto> handleUserResponse(@RequestBody @Valid ChatRequestDto request) {
        return ResponseEntity.ok(chatService.handleUserResponse(request));
    }

    @PostMapping("/descriptive")
    public ResponseEntity<OpenAIResponseDto> handleDescriptiveResponse(@RequestBody @Valid OpenAIRequestDto request) {
        return ResponseEntity.ok(openAIService.analyzeEmotion(request));
    }
}
