package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.services.ISolicitudesService;
import ar.edu.utn.frba.dds.services.IColeccionesService;
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
  private ISolicitudesService solicitudesService;
  private IColeccionesService coleccionesService;

  public AgregadorController(ISolicitudesService solicitudesService, IColeccionesService coleccionesService) {
    this.solicitudesService = solicitudesService;
    this.coleccionesService = coleccionesService;
  }


  //controllers sujetos a modificacion
  @GetMapping("/hechos")
  public List<Hecho> getHechos(
      @RequestParam (required = false) Integer page,
      @RequestParam (required = false) Integer per_page
  ) {
    //TODO agregar retorno de estado 404
    if (page != null && per_page != null) {
      return coleccionesService.obtenerHechos(page, per_page);
    } else {
      return coleccionesService.obtenerHechos();
    }
  }

  @GetMapping("/colecciones")
  public List<Coleccion> getColecciones() {
    return coleccionesService.getColecciones();
  }

  @GetMapping("/colecciones/{id}/hechos")
  public List<Hecho> getHechosDeColeccion(
      @PathVariable String id,
      @RequestParam (required = false) Integer page,
      @RequestParam (required = false) Integer per_page
  ) {
    if (page != null && per_page != null) {
      //agregar meanejo error 404
      return coleccionesService.obtenerHechos(id, page, per_page);
    } else {
      return coleccionesService.obtenerHechos(id);
    }
  }

  @PostMapping("/solicitudes")
  public void agregarSolicitud(@RequestBody Solicitud solicitud) {
    solicitudesService.createSolicitud(solicitud);
    //return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud creada");
  }
}
