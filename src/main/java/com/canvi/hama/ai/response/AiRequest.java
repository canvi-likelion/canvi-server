package com.canvi.hama.ai.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
public class AiRequest{
    @NotBlank
    private String userName;

    @NotBlank
    private String prompt;
}
