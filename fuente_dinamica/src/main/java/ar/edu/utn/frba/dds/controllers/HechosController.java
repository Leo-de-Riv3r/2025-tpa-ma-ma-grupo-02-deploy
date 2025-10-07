package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.servicies.IHechosService;
import ar.edu.utn.frba.dds.servicies.impl.HechosService;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @PostMapping
  public HechoOutputDTO crearHecho(@RequestBody HechoInputDTO hechoDto) {
    //System.out.println("Lista vacia" + hechoDto.getMultimedia().get(0).());
    return hechosService.crearHecho(hechoDto);
  }
}
