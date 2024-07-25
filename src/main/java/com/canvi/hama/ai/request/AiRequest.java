package com.canvi.hama.ai.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest{
    @NotBlank
    private String userName;

    @NotBlank
    private String prompt;
}
