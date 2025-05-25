package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.services.IAgregadorService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgregadorController {
  private IAgregadorService agregadorService;
  public AgregadorController(IAgregadorService agregadorService) {
    this.agregadorService = agregadorService;
  }
  //falta controllers
}
