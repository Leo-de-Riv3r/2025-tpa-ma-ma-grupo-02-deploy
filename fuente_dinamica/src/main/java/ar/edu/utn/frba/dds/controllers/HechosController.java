package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.repositories.impl.HechosRepository;
import ar.edu.utn.frba.dds.servicies.IHechosService;
import ar.edu.utn.frba.dds.servicies.impl.HechosService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/hechos")
public class HechosController {
  private final IHechosService hechosService;

  public HechosController(HechosService hechosService) {
    this.hechosService = hechosService;
  }

  @GetMapping
  public HechoPagDTO getHechos(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                     @RequestParam(value = "per_page", required = false, defaultValue = "10") int perPage) {
    return hechosService.getHechos(page, perPage);
  }

  @GetMapping("/{id}")
  public HechoOutputDTO getHechoById(@PathVariable Long id) {
    return hechosService.getHechoById(id); // TODO: try cath
  }
}
