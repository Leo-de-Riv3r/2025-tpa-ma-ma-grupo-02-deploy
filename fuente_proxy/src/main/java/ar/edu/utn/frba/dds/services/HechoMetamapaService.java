package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.external.metamapa.ColeccionDTO;
import ar.edu.utn.frba.dds.models.dtos.external.metamapa.HechoDTO;
import ar.edu.utn.frba.dds.models.dtos.external.metamapa.SolicitudSalidaDTO;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HechoMetamapaService {

  private final WebClient.Builder webClientBuilder;
  @Setter
  private String baseUrl;

  public HechoMetamapaService(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  private WebClient getClient() {
    if (baseUrl == null || baseUrl.isBlank()) {
      throw new IllegalStateException("La URL base no ha sido configurada. Use /metamapa/config para establecerla.");
    }
    return webClientBuilder.baseUrl(baseUrl).build();
  }

  public Flux<HechoDTO> getHechos(
      String categoria,
      String fechaReporteDesde,
      String fechaReporteHasta,
      String fechaAcontecimientoDesde,
      String fechaAcontecimientoHasta,
      String ubicacion
  ) {
    return getClient()
        .get()
        .uri(uriBuilder -> {
          UriBuilder builder = uriBuilder.path("/hechos");
          builder.queryParam("categoria", categoria);
          builder.queryParam("fechaReporteDesde", fechaReporteDesde);
          builder.queryParam("fechaReporteHasta", fechaReporteHasta);
          builder.queryParam("fechaAcontecimientoDesde", fechaAcontecimientoDesde);
          builder.queryParam("fechaAcontecimientoHasta", fechaAcontecimientoHasta);
          builder.queryParam("ubicacion", ubicacion);
          return builder.build();
        })
        .retrieve()
        .bodyToFlux(HechoDTO.class);
  }

  public Flux<ColeccionDTO> getColecciones() {
    return getClient()
        .get()
        .uri("/colecciones")
        .retrieve()
        .bodyToFlux(ColeccionDTO.class);
  }

  public Flux<HechoDTO> getHechosDeColeccion(String id) {
    return getClient()
        .get()
        .uri("/colecciones/{id}/hechos", id)
        .retrieve()
        .bodyToFlux(HechoDTO.class);
  }

  public Mono<Void> postSolicitudEliminacion(SolicitudSalidaDTO solicitud) {
    return getClient()
        .post()
        .uri("/solicitudes")
        .bodyValue(solicitud)
        .retrieve()
        .bodyToMono(Void.class);
  }
}