package ar.edu.utn.frba.dds.config;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {
  @Value("${gemini.api-key}") // Lee de tu properties/env
  private String apiKey;

  @Bean
  public GoogleAiGeminiChatModel geminiChatModel() {
    return GoogleAiGeminiChatModel.builder()
        .apiKey(apiKey)
        .modelName("gemini-2.5-flash")
        .temperature(0.0) // Analítico, sin creatividad
        .logRequestsAndResponses(false) // Útil para ver si llega info
        .build();
  }
}
