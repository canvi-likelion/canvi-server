package com.canvi.hama.domain.ai.service;

import com.canvi.hama.domain.ai.exception.AiException;
import com.canvi.hama.domain.ai.response.AiResponseStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class PapagoService {

    @Value("${papago.api.key}")
    private String papagoiApiKey;

    @Value("${papago.api.key-id}")
    private String papagoiApiKeyId;

    private static final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getPapago(String prompt) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String text = URLEncoder.encode(prompt, StandardCharsets.UTF_8.toString());
            URL url = new URL(PAPAGO_API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", papagoiApiKeyId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", papagoiApiKey);

            // Post request
            String postParams = "source=ko&target=en&text=" + text;
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(postParams);
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            } else {  // 오류 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            String responseBody = response.toString();
            Map<String, Object> responseJson = objectMapper.readValue(responseBody, Map.class);

            Map<String, Object> message = (Map<String, Object>) responseJson.get("message");
            if (message != null) {
                Map<String, Object> result = (Map<String, Object>) message.get("result");
                if (result != null) {
                    return (String) result.get("translatedText");
                }
            }
            throw new AiException(AiResponseStatus.UNPROCESSABLE_ENTITY);

        } catch (IOException e) {
            throw new AiException(AiResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
