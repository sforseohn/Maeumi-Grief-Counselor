package com.gcl.maeumi.counsel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class CounselDto {
    @Data
    @AllArgsConstructor
    public static class DialogflowRequestDto {
        private String session;
        private QueryResult queryResult;

        @Getter
        public static class QueryResult {
            private String queryText;
            private Map<String, Object> parameters;
        }
    }
}
