package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.external.hecho.HechoDTO;
import ar.edu.utn.frba.dds.models.dtos.external.hecho.HechoPaginadoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class HechoService {
  private final WebClient webClient;

  public HechoService(
      @Value("${api.baseUrl}") String baseUrl,
      ApiAuthService authService
  ) {
    String token = authService.getBearerToken();
    this.webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Authorization", "Bearer " + token)
        .build();
  }

  public Mono<HechoPaginadoResponseDTO> getHechos(Integer page, Integer perPage) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/api/desastres")
            .queryParam("page", page)
            .queryParam("per_page", perPage)
            .build())
        .retrieve()
        .bodyToMono(HechoPaginadoResponseDTO.class);
  }

  public Mono<HechoDTO> getHechoById(Integer id) {
    return webClient.get()
        .uri("/api/desastres/{id}", id)
        .retrieve()
        .bodyToMono(HechoDTO.class);
  }
}