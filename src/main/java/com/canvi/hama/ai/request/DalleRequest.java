package com.canvi.hama.ai.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DalleRequest {


    @NotBlank
    private Long diaryId;

    @NotBlank
    private String gender;

    @NotBlank
    private String age;

    @NotBlank
    private String hairStyle;

    @NotBlank
    private String clothes;

    @NotBlank
    private String prompt;

}