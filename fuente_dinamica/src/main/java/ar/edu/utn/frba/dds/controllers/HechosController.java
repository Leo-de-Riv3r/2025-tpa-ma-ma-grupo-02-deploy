package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.repositories.impl.HechosRepository;
import ar.edu.utn.frba.dds.servicies.IHechosService;
import ar.edu.utn.frba.dds.servicies.impl.HechosService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hechos")
public class HechosController {
  private IHechosService hechosService;

  public HechosController(HechosService hechosService) {
    this.hechosService = hechosService;
  }


}
