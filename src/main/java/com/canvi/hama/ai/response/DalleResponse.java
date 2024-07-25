package com.canvi.hama.ai.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DalleResponse {
    private Long statusCode;
    private Integer created;

    private Data data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String revised_prompt;
        private String url;
    }
}
