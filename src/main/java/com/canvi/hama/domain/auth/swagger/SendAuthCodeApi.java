package com.canvi.hama.domain.auth.swagger;

import com.canvi.hama.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.http.MediaType;

@Operation(summary = "인증 코드 전송")
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "인증 코드 전송 성공",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "400",
                description = "유효하지 않은 이메일 형식",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "409",
                description = "이미 존재하는 이메일",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = BaseResponse.class)))
})
public @interface SendAuthCodeApi {
}
