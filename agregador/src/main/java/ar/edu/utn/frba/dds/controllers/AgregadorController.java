package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.services.IAgregadorService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgregadorController {
  private IAgregadorService agregadorService;
  public AgregadorController(IAgregadorService agregadorService) {
    this.agregadorService = agregadorService;
  }
  //falta controllers
  @GetMapping("/hechos")
  public List<Hecho> getHechos(
      @RequestParam (required = false) Integer page,
      @RequestParam (required = false) Integer per_page
  ) {
    if (page != null && per_page != null) {
      return agregadorService.obtenerHechos(page, per_page);
    } else {
      return agregadorService.obtenerHechos();
    }
  }

  @GetMapping("/colecciones")
  public List<Coleccion> getColecciones() {
    return agregadorService.getColecciones();
  }

  @GetMapping("/colecciones/{id}/hechos")
  public List<Hecho> getHechosDeColeccion(
      @PathVariable String id,
      @RequestParam (required = false) Integer page,
      @RequestParam (required = false) Integer per_page
  ) {
    if (page != null && per_page != null) {
      return agregadorService.obtenerHechos(id, page, per_page);
    } else {
      return agregadorService.obtenerHechos(id);
    }
  }

  @PostMapping("/solicitudes")
  public ResponseEntity<String> agregarSolicitud(@RequestBody Solicitud solicitud) {
    agregadorService.createSolicitud(solicitud);
    return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud creada");
  }
}
