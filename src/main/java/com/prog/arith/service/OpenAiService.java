package com.prog.arith.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAiService {

  private final String apiKey;
  private final String apiUrl = "https://api.openai.com/v1/chat/completions";

  public OpenAiService() {
    // Récupération de la clé API depuis la variable d'environnement
    this.apiKey = System.getenv("OPENAI_API_KEY");
    if (this.apiKey == null || this.apiKey.isEmpty()) {
      throw new IllegalStateException(
          "La variable d'environnement OPENAI_API_KEY n'est pas définie");
    }
  }

  public String getMalagasyDefinition(String teny) {
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
      systemMessage.put("content", "Vous êtes un professeur de langue malgache.");
      messages.add(systemMessage);

      Map<String, String> userMessage = new HashMap<>();
      userMessage.put("role", "user");
      userMessage.put("content", "Définis le mot malgache : " + teny);
      messages.add(userMessage);

      request.put("messages", messages);

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

      ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        List<Map<String, Object>> choices =
            (List<Map<String, Object>>) response.getBody().get("choices");
        if (choices != null && !choices.isEmpty()) {
          Map<String, Object> firstChoice = choices.get(0);
          Map<String, Object> firstMessage = (Map<String, Object>) firstChoice.get("message");
          if (firstMessage != null && firstMessage.containsKey("content")) {
            return (String) firstMessage.get("content");
          }
        }
        return "Aucune définition trouvée.";
      } else {
        return "Erreur lors de la requête à l'API OpenAI.";
      }
    } catch (Exception e) {
      return "Exception : " + e.getMessage();
    }
  }
}
