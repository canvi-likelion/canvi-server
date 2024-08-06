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

    public Map<String, Object> getChatGptResponse(AiRequest request) {
        validatePrompt(request.prompt());
        String translatedPrompt = papagoService.getPapago(request.prompt());
        return callOpenAiApi(OPENAI_API_URL, createGptRequestBody(request, translatedPrompt));
    }

    public Map<String, Object> getHelpGptResponse(AiRequest request) {
        validatePrompt(request.prompt());
        String translatedPrompt = papagoService.getPapago(request.prompt());
        return callOpenAiApi(OPENAI_API_URL, createHelpRequestBody(request, translatedPrompt));
    }

    public Map<String, Object> getSummaryGptResponse(AiRequest request) {
        validatePrompt(request.prompt());
        String translatedPrompt = papagoService.getPapago(request.prompt());
        return callOpenAiApi(OPENAI_API_URL, createSummaryRequestBody(request, translatedPrompt));
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

    private Map<String, Object> createHelpRequestBody(AiRequest request, String translatedPrompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", createHelpGptMessages(request, translatedPrompt));
        return requestBody;
    }

    private Map<String, Object> createSummaryRequestBody(AiRequest request, String translatedPrompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", createSummaryGptMessages(request, translatedPrompt));
        return requestBody;
    }


    private List<Map<String, String>> createHelpGptMessages(AiRequest request, String translatedPrompt) {
        return List.of(
                Map.of("role", "system", "content", createHelpSystemMessage(request)),
                Map.of("role", "user", "content", translatedPrompt)
        );
    }

    private List<Map<String, String>> createSummaryGptMessages(AiRequest request, String translatedPrompt) {
        return List.of(
                Map.of("role", "system", "content", createSummarySystemMessage(request)),
                Map.of("role", "user", "content", "It's about the chat.\n" + translatedPrompt)
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

    private String createHelpSystemMessage(AiRequest request) {
        return "User name is " + request.username() + " " +
                "The user is having a hard time keeping a diary.\n" +
                "You should ask or inform the user of questions or contents that can help you write a diary, and also inform the user of questions that can help you write a diary.\n" +
                "Please answer in honorifics in Korean.";
    }

    private String createSummarySystemMessage(AiRequest request) {
        return "User name is " + request.username() + " " +
                "This is the conversation I had with ai through chat. Briefly summarize it in sentences\n" +
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
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(
                        "I will tell you the contents of my diary, so please analyze them and draw them with emotional and cute pictures. ")
                .append("Choose one of the contents of the diary and draw it. My information is as follows. ");

        appendAttribute(messageBuilder, "Gender", request.gender());
        appendAttribute(messageBuilder, "Age", request.age());
        appendAttribute(messageBuilder, "HairStyle", request.hairStyle());
        appendAttribute(messageBuilder, "Clothes", request.clothes());

        messageBuilder.append("The contents of my diary are as follows.. ").append(translatedPrompt);

        return messageBuilder.toString();
    }

    private void appendAttribute(StringBuilder builder, String attributeName, String attributeValue) {
        if (attributeValue != null && !attributeValue.isBlank()) {
            builder.append("My ").append(attributeName).append(" is ").append(attributeValue).append(" ");
        }
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
