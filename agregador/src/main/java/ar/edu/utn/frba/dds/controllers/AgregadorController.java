package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.CambioAlgoritmoDTO;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.FiltroDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.SolicitudDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import ar.edu.utn.frba.dds.models.entities.factories.FiltroStrategyFactory;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroCategoria;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFechaAcontecimiento;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFechaReporte;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroUbicacion;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.FuenteService;
import ar.edu.utn.frba.dds.services.SolicitudService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AgregadorController {
  private final SolicitudService solicitudService;
  private final ColeccionService coleccionService;

  public AgregadorController(SolicitudService solicitudService, ColeccionService coleccionService) {
    this.solicitudService = solicitudService;
    this.coleccionService = coleccionService;
  }

  //COLECCIONES

  //CRUD COLECCIONES
  @PostMapping("/colecciones")
  public ResponseEntity<Coleccion> createColeccion(@RequestBody ColeccionDTOEntrada dto) {
    Coleccion coleccionCreada = coleccionService.createColeccion(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(coleccionCreada);
  }

  @GetMapping("/colecciones")
  public List<Coleccion> getColecciones() {
    return coleccionService.getColecciones();
  }

  @GetMapping("/colecciones/{id}")
  public Coleccion getColeccion(@PathVariable String id) {
    return coleccionService.getColeccion(id);
  }

  @PutMapping("/colecciones/{id}")
  public void updateColeccion(@PathVariable String id, @RequestParam ColeccionDTOEntrada dto) {
    coleccionService.updateColeccion(id, dto);
  }

  @DeleteMapping("/colecciones/{id}")
  public void deleteColeccion(@PathVariable String id) {
    coleccionService.deleteColeccion(id);
  }

  @GetMapping("/hechos")
  public Set<Hecho> getHechos(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer per_page
  ) {
    Set<Hecho> hechos = coleccionService.getHechos(null, false, page, per_page, null);
    return hechos.stream().filter(hecho -> !solicitudService.hechoEliminado(hecho)).collect(Collectors.toSet());
  }

  @GetMapping("/colecciones/{id}/hechos")
  public Set<Hecho> getHechos(
      @PathVariable String id,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer per_page,
      @RequestParam(required = false, defaultValue = "false") Boolean curados,
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) LocalDateTime fecha_reporte_desde,
      @RequestParam(required = false) LocalDateTime fecha_reporte_hasta,
      @RequestParam(required = false) LocalDateTime fecha_acontecimiento_desde,
      @RequestParam(required = false) LocalDateTime fecha_acontecimiento_hasta,
      @RequestParam(required = false) String ubicacion
  ) {
    Set<IFiltroStrategy> filtros = FiltroStrategyFactory.fromParams(
        categoria,
        fecha_reporte_desde,
        fecha_reporte_hasta,
        fecha_acontecimiento_desde,
        fecha_acontecimiento_hasta,
        ubicacion
    );

    Set<Hecho> hechos = coleccionService.getHechos(id, curados, page, per_page, filtros);
    return hechos.stream().filter(hecho -> !solicitudService.hechoEliminado(hecho)).collect(Collectors.toSet());
  }

  @PutMapping("/colecciones/{id}/algoritmo")
  public void updateAlgoritmoConsenso(@PathVariable String id, @RequestBody CambioAlgoritmoDTO algoritmoDTO) {
    coleccionService.updateAlgoritmoConsenso(id, algoritmoDTO);
  }

  @PostMapping("/colecciones/{id}/fuentes")
  public void addFuente(@PathVariable String id, @RequestBody FuenteDTO dto) {
    coleccionService.addFuente(id, dto);
  }

  @DeleteMapping("/colecciones/{id}/fuentes")
  public void removeFuente(@PathVariable String id, @RequestParam String fuenteId) {
    coleccionService.removeFuente(id, fuenteId);
  }

  //prueba
  @PutMapping("/colecciones")
  public void actualizarHechosCurados() {
    coleccionService.refrescarHechosCurados();
  }

  //SOLICITUDES
  @PostMapping("/solicitudes")
  public ResponseEntity<String> agregarSolicitud(@RequestBody SolicitudDTOEntrada dto) {
    Solicitud solicitud = new Solicitud();
    solicitud.setTitulo(dto.getTitulo());
    solicitud.setTexto(dto.getTexto());
    solicitud.setTituloHecho(dto.getTituloHecho());
    solicitud.setResponsable(dto.getResponsable());
    solicitudService.createSolicitud(solicitud);
    return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud creada");
  }

  @PutMapping("/solicitudes/{id}/aceptar")
  public void aceptarSolicitud(
      @PathVariable String id,
      @RequestParam(required = true) String supervisor
  ) {
    solicitudService.aceptarSolicitud(id, supervisor);
  }

  @PutMapping("/solicitudes/{id}/denegar")
  public void rechazarSolicitud(
      @PathVariable String id,
      @RequestParam(required = true) String supervisor
  ) {
    solicitudService.rechazarSolicitud(id, supervisor);
  }
}
