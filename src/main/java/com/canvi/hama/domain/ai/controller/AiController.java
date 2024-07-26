package com.canvi.hama.domain.ai.controller;

import com.canvi.hama.domain.ai.request.DalleRequest;
import com.canvi.hama.domain.ai.request.AiRequest;
import com.canvi.hama.domain.ai.response.DalleResponse;
import com.canvi.hama.domain.ai.response.GptResponse;
import com.canvi.hama.domain.ai.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AiController {

    private final GptService aiService;

    @Autowired
    public AiController(GptService aiService) {
        this.aiService = aiService;
    }


    @PostMapping("/gpt")
    public ResponseEntity<?> getChatGptResponse(@RequestBody AiRequest request) {

        GptResponse gptResponse = new GptResponse(201L, new GptResponse.Data(aiService.getChatGptResponse(request)));

        return ResponseEntity.status(HttpStatus.CREATED).body(gptResponse);
    }

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