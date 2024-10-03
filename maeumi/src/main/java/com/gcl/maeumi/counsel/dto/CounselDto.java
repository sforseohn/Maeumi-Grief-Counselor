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
    public class DialogflowRequestDto {
        private String session;
        private QueryResult queryResult;

        public static class QueryResult {
            private Map<String, Object> parameters;

            public Map<String, Object> getParameters() {
                return parameters;
            }
        }
    }
}
