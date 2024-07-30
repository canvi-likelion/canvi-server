package com.canvi.hama.domain.user.swagger;

import com.canvi.hama.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Operation(summary = "프로필 이미지 업데이트")
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "업데이트 성공",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404",
                description = "존재하지 않는 사용자",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "500",
                description = "데이터베이스 저장 오류",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = BaseResponse.class)))
})
public @interface ProfileImageUpdateApi {
}
