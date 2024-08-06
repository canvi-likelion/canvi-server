package com.canvi.hama.domain.ai.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GptResponse {
    private Long statusCode;
    private Data data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String gptResult;

    }
}
