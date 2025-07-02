package ar.edu.utn.frba.dds.controllers;

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
  @PostMapping
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
      @RequestParam(required = false) Boolean curados,
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) LocalDateTime fecha_reporte_desde,
      @RequestParam(required = false) LocalDateTime fecha_reporte_hasta,
      @RequestParam(required = false) LocalDateTime fecha_acontecimiento_desde,
      @RequestParam(required = false) LocalDateTime fecha_acontecimiento_hasta,
      @RequestParam(required = false) String ubicacion
  ) {
    Set<IFiltroStrategy> filtros = new HashSet<>();

    if (categoria != null) filtros.add(new FiltroCategoria(categoria));
    if (fecha_acontecimiento_desde != null || fecha_acontecimiento_hasta != null)
      filtros.add(new FiltroFechaAcontecimiento(fecha_acontecimiento_desde, fecha_acontecimiento_hasta));
    if (fecha_reporte_desde != null || fecha_reporte_hasta != null)
      filtros.add(new FiltroFechaReporte(fecha_reporte_desde, fecha_reporte_hasta));
    if (ubicacion != null && ubicacion.contains(",")) {
      try {
        String[] partes = ubicacion.split(",");
        Double latitud = Double.parseDouble(partes[0].trim());
        Double longitud = Double.parseDouble(partes[1].trim());
        filtros.add(new FiltroUbicacion(latitud, longitud));
      } catch (NumberFormatException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ubicación malformateada. Usar 'latitud,longitud'");
      }
    }
    Set<Hecho> hechos = coleccionService.getHechos(id, curados, page, per_page, filtros);
    return hechos.stream().filter(hecho -> !solicitudService.hechoEliminado(hecho)).collect(Collectors.toSet());
  }

  @PostMapping("/colecciones/{id}/filtros")
  public ResponseEntity<String> addCriterio(
      @PathVariable String id,
      @RequestBody FiltroDTOEntrada dto
  ) {
    IFiltroStrategy filtro = FiltroStrategyFactory.fromDTO(dto);
    coleccionService.addCriterio(id, filtro);
    return ResponseEntity.ok("Filtro agregado correctamente");
  }

  @DeleteMapping("/colecciones/{id}/filtros")
  public ResponseEntity<String> removeCriterio(
      @PathVariable String id,
      @RequestBody FiltroDTOEntrada dto
  ) {
    IFiltroStrategy filtro = FiltroStrategyFactory.fromDTO(dto);
    coleccionService.removeCriterio(id, filtro);
    return ResponseEntity.ok("Filtro eliminado correctamente");
  }

  @PutMapping("/colecciones/{id}/algoritmo")
  public void updateAlgoritmoConsenso(@PathVariable String id, @RequestParam TipoAlgoritmo tipo_algoritmo) {
    coleccionService.updateAlgoritmoConsenso(id, tipo_algoritmo);
  }

  @PostMapping("/colecciones/{id}/fuentes")
  public void addFuente(@PathVariable String id, @RequestBody FuenteDTO dto) {
    coleccionService.addFuente(id, dto);
  }

  @DeleteMapping("/colecciones/{id}/fuentes")
  public void removeFuente(@PathVariable String id, @RequestParam String fuenteId) {
    coleccionService.removeFuente(id, fuenteId);
  }

  //SOLICITUDES
  @PostMapping("/solicitudes")
  public ResponseEntity<String> agregarSolicitud(@RequestBody SolicitudDTOEntrada dto) {
    Solicitud solicitud = new Solicitud(dto.getTitulo(), dto.getTexto(), dto.getTituloHecho(), dto.getResponsable());

    solicitudService.createSolicitud(solicitud);
    return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud creada");
  }

  @PutMapping("/solicitudes/aceptar/{id}")
  public void aceptarSolicitud(
      @PathVariable String id,
      @RequestParam(required = true) String supervisor
  ) {
    solicitudService.aceptarSolicitud(id, supervisor);
  }

  @PutMapping("/solicitudes/denegar/{id}")
  public void rechazarSolicitud(
      @PathVariable String id,
      @RequestParam(required = true) String supervisor
  ) {
    solicitudService.rechazarSolicitud(id, supervisor);
  }
}
