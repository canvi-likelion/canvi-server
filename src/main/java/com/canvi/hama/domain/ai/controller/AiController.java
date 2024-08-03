package com.canvi.hama.domain.ai.controller;

import com.canvi.hama.domain.ai.dto.request.AiRequest;
import com.canvi.hama.domain.ai.dto.request.DalleRequest;
import com.canvi.hama.domain.ai.dto.response.DalleResponse;
import com.canvi.hama.domain.ai.dto.response.GptResponse;
import com.canvi.hama.domain.ai.service.GptService;
import com.canvi.hama.domain.ai.swagger.DalleApi;
import com.canvi.hama.domain.ai.swagger.GptApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OpenAI")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiController {

    private final GptService gptService;

    @GptApi
    @PostMapping("/gpt")
    public ResponseEntity<GptResponse> getChatGptResponse(@RequestBody AiRequest request) {
        Map<String, Object> gptResponseContent = gptService.getChatGptResponse(request);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) gptResponseContent.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        GptResponse gptResponse = createGptResponse(message.get("content").toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(gptResponse);
    }

    @DalleApi
    @PostMapping("/dalle")
    public ResponseEntity<DalleResponse> getDallEResponse(@RequestBody DalleRequest request) {
        Map<String, Object> dalleResult = gptService.getDallEResponse(request);
        DalleResponse dalleResponse = createDalleResponse(dalleResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(dalleResponse);
    }

    private GptResponse createGptResponse(String content) {
        return new GptResponse(201L, new GptResponse.Data(content));
    }

    private DalleResponse createDalleResponse(Map<String, Object> dalleResult) {
        Integer created = (Integer) dalleResult.get("created");
        List<Map<String, String>> dataList = (List<Map<String, String>>) dalleResult.get("data");
        Map<String, String> dataMap = dataList.get(0);
        DalleResponse.Data data = new DalleResponse.Data(dataMap.get("revised_prompt"), dataMap.get("url"));
        return new DalleResponse(201L, created, data);
    }
}
