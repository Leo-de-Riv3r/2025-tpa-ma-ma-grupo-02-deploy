package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.external.api.hecho.HechoDTO;
import ar.edu.utn.frba.dds.services.HechoApiService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/hechos")
public class HechoApiController {
  private final HechoApiService hechoApiService;

  public HechoApiController(HechoApiService hechoApiService) {
    this.hechoApiService = hechoApiService;
  }

  @GetMapping("/{id}")
  public Mono<HechoDTO> getHechoById(@PathVariable Integer id) {
    return hechoApiService.getHechoById(id);
  }

  @GetMapping
  public Flux<HechoDTO> getHechos() {
    return hechoApiService.getHechos();
  }

  @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
  public Mono<Void> handleUnconfiguredEndpoints() {
    // Lanzamos una excepción de estado que Spring WebFlux captura
    // y convierte a una respuesta HTTP 404 limpia.
    return Mono.error(new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Ruta no configurada en /api/hechos. Verifica el ID o la ruta."
    ));
  }
}