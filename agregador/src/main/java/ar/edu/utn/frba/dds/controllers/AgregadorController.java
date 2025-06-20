package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.AdministracionSolicitudDTO;
import ar.edu.utn.frba.dds.models.dtos.CambiarConsensoDTOInput;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.services.ISolicitudesService;
import ar.edu.utn.frba.dds.services.IColeccionesService;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

      Set<Hecho> hechos;
      if (page != null && per_page != null) {
        hechos = coleccionesService.obtenerHechos(page, per_page);
      } else {
        hechos = coleccionesService.obtenerHechos();
      }
      return hechos.stream().filter(hecho -> !solicitudesService.hechoEliminado(hecho)).toList();
  }

  @GetMapping("/colecciones")
  public List<Coleccion> getColecciones() {
    return coleccionesService.getColecciones();
  }

  @GetMapping("/colecciones/{id}/hechos")
  public List<Hecho> getHechosDeColeccion(
      @PathVariable String id,
      @RequestParam (required = false) Integer page,
      @RequestParam (required = false) Integer per_page,
      @RequestParam (required = false) Boolean curados
  ) {
    if (curados) {
      return coleccionesService.obtenerHechosCurados(id).stream().filter(hecho -> !solicitudesService.hechoEliminado(hecho)).toList();
    }
    else {
      Set<Hecho> hechos;
      if (page != null && per_page != null) {
        //agregar meanejo error 404
        hechos = coleccionesService.obtenerHechos(id, page, per_page);
      } else {
        hechos = coleccionesService.obtenerHechos(id);
      }
      return hechos.stream().filter(hecho -> !solicitudesService.hechoEliminado(hecho)).toList();
    }
  }

  @PostMapping("/solicitudes")
  public void agregarSolicitud(@RequestBody Solicitud solicitud) {
    solicitudesService.createSolicitud(solicitud);
    //return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud creada");
  }

  @PutMapping("/cambiar_algoritmo")
  public void cambiarAlgoritmoConsenso(@RequestBody CambiarConsensoDTOInput cambiarConsensoDTOInput) {
    coleccionesService.cambiarAlgoritmoConsenso(cambiarConsensoDTOInput.getId(), cambiarConsensoDTOInput.getAlgoritmoConsenso());
  }

  @PutMapping("/solicitudes/denegar/{id}")
  public void denegarSolicitud(
      @RequestBody AdministracionSolicitudDTO datosSupervisor,
      @RequestParam (required = true) String idSolicitud) {
    solicitudesService.rechazarSolicitud(idSolicitud, datosSupervisor.getNombre());
  }

  @PutMapping("/solicitudes/aceptar/{id}")
  public void aceptarSolicitud(
      @RequestBody AdministracionSolicitudDTO datosSupervisor,
      @RequestParam (required = true) String idSolicitud) {
    solicitudesService.aceptarSolicitud(idSolicitud, datosSupervisor.getNombre());
  }
}
