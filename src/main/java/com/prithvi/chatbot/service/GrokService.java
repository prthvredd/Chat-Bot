package com.prithvi.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GrokService {

    @Value("${grok.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String callGrok(String message) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You are a SmartNotes AI assistant. Summarize or explain clearly in simple terms."
                        ),
                        Map.of(
                                "role", "user",
                                "content", message
                        )
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map responseBody = response.getBody();

            if (responseBody == null || responseBody.get("choices") == null) {
                return "Error: AI response was empty";
            }

            Map choice = ((List<Map>) responseBody.get("choices")).get(0);
            Map messageMap = (Map) choice.get("message");
            return messageMap.get("content").toString();
        } catch (HttpStatusCodeException e) {
            HttpStatusCode statusCode = e.getStatusCode();
            return "Error: AI API returned " + statusCode.value() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "Error: Unable to get response from AI - " + e.getMessage();
        }
    }
}
