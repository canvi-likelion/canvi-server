package com.canvi.hama.ai.controller;

import com.canvi.hama.ai.request.DalleRequest;
import com.canvi.hama.ai.response.AiRequest;
import com.canvi.hama.ai.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AiController {

    private final GptService aiService;

    @Autowired
    public AiController(GptService aiService) {
        this.aiService = aiService;
    }


    @PostMapping("/gpt")
    public String getChatGptResponse(@RequestBody AiRequest request) {

        return aiService.getChatGptResponse(request);
    }

    @PostMapping("/dalle")
    public String getDallEResponse(@RequestBody DalleRequest request) {

        return aiService.getDallEResponse(request);
    }
}