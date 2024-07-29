package com.canvi.hama.domain.ai.controller;

import com.canvi.hama.domain.ai.request.AiRequest;
import com.canvi.hama.domain.ai.request.DalleRequest;
import com.canvi.hama.domain.ai.response.DalleResponse;
import com.canvi.hama.domain.ai.response.GptResponse;
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

    private final GptService aiService;

    @GptApi
    @PostMapping("/gpt")
    public ResponseEntity<?> getChatGptResponse(@RequestBody AiRequest request) {

        GptResponse gptResponse = new GptResponse(201L, new GptResponse.Data(aiService.getChatGptResponse(request)));

        return ResponseEntity.status(HttpStatus.CREATED).body(gptResponse);
    }

    @DalleApi
    @PostMapping("/dalle")
    public ResponseEntity<?> getDallEResponse(@RequestBody DalleRequest request) {
        Map<String, Object> dalleResult = aiService.getDallEResponse(request);

        Integer created = (Integer) dalleResult.get("created");
        List<Map<String, String>> dataList = (List<Map<String, String>>) dalleResult.get("data");

        Map<String, String> dataMap = dataList.get(0);

        DalleResponse.Data data = new DalleResponse.Data(dataMap.get("revised_prompt"), dataMap.get("url"));
        DalleResponse dalleResponse = new DalleResponse(201L, created, data);

        return ResponseEntity.status(HttpStatus.CREATED).body(dalleResponse);
    }
}
