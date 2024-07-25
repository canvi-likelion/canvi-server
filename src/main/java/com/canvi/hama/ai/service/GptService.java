package com.canvi.hama.ai.service;

import com.canvi.hama.ai.exception.AiException;
import com.canvi.hama.ai.request.DalleRequest;
import com.canvi.hama.ai.request.AiRequest;
import com.canvi.hama.ai.response.AiResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GptService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DALLE_API_URL = "https://api.openai.com/v1/images/generations";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PapagoService papagoService;

    @Autowired
    public GptService(PapagoService papagoService) {
        this.papagoService = papagoService;
    }

    public String getChatGptResponse(AiRequest request) {
        validationPrompt(request.getPrompt());

        String translatedPrompt = papagoService.getPapago(request.getPrompt());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(OPENAI_API_URL);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpPost.setHeader("Authorization", "Bearer " + openaiApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o");

            List<Map<String, String>> messages = List.of(
                    Map.of("role", "system", "content", "You are assistant who helps me write my diary." +
                            "Please analyze the user's diary in detail and give me feedback." +
                            "User name is " + request.getUserName() +
                            "Tell me in a warm way." +
                            "Tell me in long sentences, not in a list" +
                            "Please just write it in text." +
                            "Please answer in honorifics in Korean."),

                    Map.of("role", "user", "content", "The contents of the diary are as follows.\n" + translatedPrompt)
            );

            requestBody.put("messages", messages);

            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(requestBody), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Map<String, Object> responseJson = objectMapper.readValue(responseBody, Map.class);

                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseJson.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

                return (String) message.get("content");
            }
        } catch (IOException e) {
            throw new AiException(AiResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> getDallEResponse(DalleRequest request) {
        validationPrompt(request.getPrompt());

        String translatedPrompt = papagoService.getPapago(request.getPrompt());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost httpPost = new HttpPost(DALLE_API_URL);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + openaiApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "dall-e-3");
//            requestBody.put("prompt", "I'll tell you the contents of the diary, so please draw an emotional and cute picture that goes well with this diary.\n" +
//                    "The drawing style should be an emotional one, and the contents of the diary are as follows. \n" +
//                    "My information is as follows." +
//                    "Gender: " + request.getGender() +
//                    "\nAge: " + request.getAge() +
//                    "\nHair style: " + request.getHairStyle() +
//                    "\nClothes:" + request.getClothes() +
//                    "\nThere should be no writing on the picture.\n" +
//                    "The contents of the diary are as follows.\n" +
//                    translatedPrompt);
            requestBody.put("prompt", "I will tell you the contents of my diary, so please analyze them and draw them with emotional and cute pictures. Choose one of the contents of the diary and draw it. My information is as follows. " +
                    "My Gender is " + request.getGender() +
                    "My Age is " + request.getAge() +
                    "My Hair style is " + request.getHairStyle() +
                    "My clothes is" + request.getClothes() +
                    "The contents of my diary are as follows.." +
                    translatedPrompt);
            requestBody.put("n", 1);
            requestBody.put("size", "1024x1024");

            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(requestBody));
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                Map<String, Object> responseJson = objectMapper.readValue(responseBody, Map.class);


                return responseJson;
            }
        } catch (IOException e) {
            throw new AiException(AiResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validationPrompt(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new AiException(AiResponseStatus.BAD_REQUEST);
        }
    }
}
