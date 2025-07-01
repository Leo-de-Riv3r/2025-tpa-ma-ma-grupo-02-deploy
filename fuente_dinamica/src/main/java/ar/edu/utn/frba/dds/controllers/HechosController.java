package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.repositories.impl.HechosRepository;
import ar.edu.utn.frba.dds.servicies.IHechosService;
import ar.edu.utn.frba.dds.servicies.impl.HechosService;
import java.util.List;
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
  public List<HechoOutputDTO> getHechos() {
    return hechosService.getHechos();
  }

  @GetMapping("/{id}")
  public HechoOutputDTO getHechoById(@PathVariable Long id) {
    return hechosService.getHechoById(id); // TODO: try cath
  }
}
