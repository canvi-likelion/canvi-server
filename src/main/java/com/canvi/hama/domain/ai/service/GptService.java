package com.canvi.hama.domain.ai.service;

import com.canvi.hama.domain.ai.dto.request.AiRequest;
import com.canvi.hama.domain.ai.dto.request.DalleRequest;
import com.canvi.hama.domain.ai.enums.AiResponseStatus;
import com.canvi.hama.domain.ai.exception.AiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GptService {

    private final RestTemplate restTemplate;
    private final PapagoService papagoService;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DALLE_API_URL = "https://api.openai.com/v1/images/generations";

    public String getChatGptResponse(AiRequest request) {
        validatePrompt(request.prompt());
        String translatedPrompt = papagoService.getPapago(request.prompt());
        return callOpenAiApi(OPENAI_API_URL, createGptRequestBody(request, translatedPrompt));
    }

    public Map<String, Object> getDallEResponse(DalleRequest request) {
        validatePrompt(request.prompt());
        String translatedPrompt = papagoService.getPapago(request.prompt());
        return callOpenAiApi(DALLE_API_URL, createDalleRequestBody(request, translatedPrompt));
    }

    private void validatePrompt(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new AiException(AiResponseStatus.BAD_REQUEST);
        }
    }

    private Map<String, Object> createGptRequestBody(AiRequest request, String translatedPrompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", createGptMessages(request, translatedPrompt));
        return requestBody;
    }

    private List<Map<String, String>> createGptMessages(AiRequest request, String translatedPrompt) {
        return List.of(
                Map.of("role", "system", "content", createSystemMessage(request)),
                Map.of("role", "user", "content", "The contents of the diary are as follows.\n" + translatedPrompt)
        );
    }

    private String createSystemMessage(AiRequest request) {
        return "You are assistant who helps me write my diary. " +
                "Please analyze the user's diary in detail and give me feedback. " +
                "User name is " + request.username() + " " +
                "Tell me in a warm way. " +
                "Tell me in long sentences, not in a list " +
                "Please just write it in text. " +
                "Please answer in honorifics in Korean.";
    }

    private Map<String, Object> createDalleRequestBody(DalleRequest request, String translatedPrompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "dall-e-3");
        requestBody.put("prompt", createDallePrompt(request, translatedPrompt));
        requestBody.put("n", 1);
        requestBody.put("size", "1024x1024");
        return requestBody;
    }

    private String createDallePrompt(DalleRequest request, String translatedPrompt) {
        return "I will tell you the contents of my diary, so please analyze them and draw them with emotional and cute pictures. "
                +
                "Choose one of the contents of the diary and draw it. My information is as follows. " +
                "My Gender is " + request.gender() + " " +
                "My Age is " + request.age() + " " +
                "My Hair style is " + request.hairStyle() + " " +
                "My clothes is " + request.clothes() + " " +
                "The contents of my diary are as follows.. " + translatedPrompt;
    }

    private <T> T callOpenAiApi(String url, Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            return objectMapper.readValue(response.getBody(), new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            throw new AiException(AiResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
