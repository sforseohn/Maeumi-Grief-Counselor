package com.gcl.maeumi.counsel.service;

import jakarta.persistence.Embeddable;

@Embeddable
public class ResponseData {
    private Integer questionId;
    private String userResponse;

    public ResponseData() {}

    public ResponseData(Integer questionId, String userResponse) {
        this.questionId = questionId;
        this.userResponse = userResponse;
    }
}
