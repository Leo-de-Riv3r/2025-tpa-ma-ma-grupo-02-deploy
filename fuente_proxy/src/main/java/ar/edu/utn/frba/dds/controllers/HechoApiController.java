package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.external.api.hecho.HechoDTO;
import ar.edu.utn.frba.dds.models.dtos.external.api.hecho.HechosPagDTO;
import org.springframework.web.bind.annotation.*;
import ar.edu.utn.frba.dds.services.HechoApiService;
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
  public Mono<HechosPagDTO> getHechos(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer per_page
  ) {
    return hechoApiService.getHechos(page, per_page);
  }
}