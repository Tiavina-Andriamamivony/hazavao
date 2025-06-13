package com.prog.arith.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OpenAiService {

  private static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);

  private final String apiKey;
  private final String apiUrl = "https://api.openai.com/v1/chat/completions";

  public OpenAiService() {
    this.apiKey = System.getenv("OPENAI_API_KEY");
    if (this.apiKey == null || this.apiKey.isEmpty()) {
      logger.warn("Tsy voafaritra ny fanalahidy API OPENAI_API_KEY");
    }
  }

  public String getMalagasyDefinition(String teny) {
    if (apiKey == null || apiKey.isEmpty()) {
      return "Tsy afaka mifandray amin'ny API OpenAI: tsy voafaritra ny fanalahidy API";
    }

    try {
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(apiKey);

      Map<String, Object> request = new HashMap<>();
      request.put("model", "gpt-3.5-turbo");

      List<Map<String, String>> messages = new ArrayList<>();

      Map<String, String> systemMessage = new HashMap<>();
      systemMessage.put("role", "system");
      systemMessage.put("content", "Mpampianatra teny malagasy ianao. Omeo famaritana mazava sy fohifohy amin'ny teny malagasy ireo teny anontaniana anao. Aza mampiasa teny vahiny fa teny malagasy ihany.");
      messages.add(systemMessage);

      Map<String, String> userMessage = new HashMap<>();
      userMessage.put("role", "user");
      userMessage.put("content", "Inona no dikan'ny teny malagasy hoe: " + teny);
      messages.add(userMessage);

      request.put("messages", messages);

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

      ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        if (choices != null && !choices.isEmpty()) {
          Map<String, Object> firstChoice = choices.get(0);
          Map<String, Object> firstMessage = (Map<String, Object>) firstChoice.get("message");
          if (firstMessage != null && firstMessage.containsKey("content")) {
            return (String) firstMessage.get("content");
          }
        }
        return "Tsy nahitana famaritana.";
      } else {
        return "Nisy olana tamin'ny fangatahana tamin'ny API OpenAI.";
      }
    } catch (Exception e) {
      return "Nisy hadisoana: " + e.getMessage();
    }
  }
}
