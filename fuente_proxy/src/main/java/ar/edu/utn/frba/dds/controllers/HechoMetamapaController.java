package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.external.metamapa.ColeccionDTO;
import ar.edu.utn.frba.dds.models.dtos.external.metamapa.HechoDTO;
import ar.edu.utn.frba.dds.models.dtos.external.metamapa.SolicitudSalidaDTO;
import ar.edu.utn.frba.dds.services.HechoMetamapaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/metamapa")
public class HechoMetamapaController {

  private final HechoMetamapaService hechoMetamapaService;

  public HechoMetamapaController(HechoMetamapaService hechoMetamapaService) {
    this.hechoMetamapaService = hechoMetamapaService;
  }

  @PostMapping("/config")
  public ResponseEntity<String> configurarBaseUrl(@RequestParam String baseUrl) {
    hechoMetamapaService.setBaseUrl(baseUrl);
    return ResponseEntity.ok("URL base configurada correctamente: " + baseUrl);
  }

  @GetMapping("/hechos")
  public Flux<HechoDTO> getHechos(
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) String fecha_reporte_desde,
      @RequestParam(required = false) String fecha_reporte_hasta,
      @RequestParam(required = false) String fecha_acontecimiento_desde,
      @RequestParam(required = false) String fecha_acontecimiento_hasta,
      @RequestParam(required = false) String ubicacion
  ) {
    return hechoMetamapaService.getHechos(
        categoria,
        fecha_reporte_desde,
        fecha_reporte_hasta,
        fecha_acontecimiento_desde,
        fecha_acontecimiento_hasta,
        ubicacion
    );
  }

  @GetMapping("/colecciones")
  public Flux<ColeccionDTO> getColecciones() {
    return hechoMetamapaService.getColecciones();
  }

  @GetMapping("/colecciones/{id}/hechos")
  public Flux<HechoDTO> getHechosDeColeccion(@PathVariable String id) {
    return hechoMetamapaService.getHechosDeColeccion(id);
  }

  @PostMapping("/solicitudes")
  public Mono<Void> postSolicitudEliminacion(@RequestBody SolicitudSalidaDTO solicitud) {
    return hechoMetamapaService.postSolicitudEliminacion(solicitud);
  }
}