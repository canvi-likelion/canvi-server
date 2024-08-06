package com.canvi.hama.domain.diary.swagger.image;

import com.canvi.hama.common.response.BaseResponse;
import com.canvi.hama.domain.diary.entity.Comment;
import com.canvi.hama.domain.diary.entity.Image;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Operation(summary = "Image 불러오기")
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = Image.class)))
})
public @interface GetImageApi {
}
