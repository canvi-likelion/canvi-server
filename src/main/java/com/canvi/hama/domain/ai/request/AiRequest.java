package com.canvi.hama.domain.ai.request;

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
