package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.external.hecho.HechoDTO;
import ar.edu.utn.frba.dds.models.dtos.external.hecho.HechosPagDTO;
import org.springframework.web.bind.annotation.*;
import ar.edu.utn.frba.dds.services.HechoService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/hechos")
public class HechoController {
  private final HechoService hechoService;

  public HechoController(HechoService hechoService) {
    this.hechoService = hechoService;
  }

  @GetMapping("/{id}")
  public Mono<HechoDTO> getHechoById(@PathVariable Integer id) {
    return hechoService.getHechoById(id);
  }

  @GetMapping
  public Mono<HechosPagDTO> getHechos(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer per_page
  ) {
    return hechoService.getHechos(page, per_page);
  }
}