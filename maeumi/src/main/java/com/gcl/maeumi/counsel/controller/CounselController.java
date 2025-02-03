package com.gcl.maeumi.counsel.controller;

import com.gcl.maeumi.counsel.dto.CounselDto.DialogflowRequestDto;
import com.gcl.maeumi.counsel.service.CounselService;
import com.gcl.maeumi.user.entity.User;
import com.gcl.maeumi.user.repository.UserRepository;
import com.gcl.maeumi.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/counsels")
public class CounselController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CounselService counselService;

    @PostMapping("/addResponse")
    public ApiResponse<String> addResponse(@RequestBody DialogflowRequestDto request) {
        long userId = Long.parseLong((String) request.getQueryResult().getParameters().get("userId"));
        Integer sessionNumber = Integer.parseInt((String) request.getQueryResult().getParameters().get("sessionNumber"));

        Optional<User> member = userRepository.findById(userId);
        if (!member.isPresent()) {
            return ApiResponse.createFail(userId + "를 id로 가지는 사용자가 존재하지 않습니다.");
        }

        counselService.processUserResponse(request);

        return ApiResponse.createSuccess(request.toString(),"응답이 성공적으로 저장되었습니다.");
    }
}