package com.canvi.hama.domain.ai.service;

import com.canvi.hama.domain.ai.enums.AiResponseStatus;
import com.canvi.hama.domain.ai.exception.AiException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PapagoService {

    private final RestTemplate restTemplate;

    @Value("${papago.api.key}")
    private String papagoApiKey;

    @Value("${papago.api.key-id}")
    private String papagoApiKeyId;

    private static final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

    public String getPapago(String prompt) {
        HttpHeaders headers = createHeaders();
        String encodedPrompt = encodePrompt(prompt);
        HttpEntity<String> entity = new HttpEntity<>(createRequestBody(encodedPrompt), headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    PAPAGO_API_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            return extractTranslatedText(response.getBody());
        } catch (RestClientException e) {
            throw new AiException(AiResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", papagoApiKeyId);
        headers.set("X-NCP-APIGW-API-KEY", papagoApiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private String encodePrompt(String prompt) {
        try {
            return URLEncoder.encode(prompt, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new AiException(AiResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String createRequestBody(String encodedPrompt) {
        return String.format("source=ko&target=en&text=%s", encodedPrompt);
    }

    private String extractTranslatedText(Map<String, Object> responseBody) {
        Map<String, Object> message = (Map<String, Object>) responseBody.get("message");
        if (message != null) {
            Map<String, Object> result = (Map<String, Object>) message.get("result");
            if (result != null) {
                return (String) result.get("translatedText");
            }
        }
        throw new AiException(AiResponseStatus.UNPROCESSABLE_ENTITY);
    }
}
