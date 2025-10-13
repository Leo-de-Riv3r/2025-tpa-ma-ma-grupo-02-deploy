package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.CambioAlgoritmoDTO;
import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.input.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.input.FiltroDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.input.HechoUpdateDto;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.input.SolicitudDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDto;
import ar.edu.utn.frba.dds.models.dtos.output.ResumenActividadDto;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudDTOOutput;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudResumenDtoOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.factories.FiltroStrategyFactory;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.SolicitudService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgregadorController {
  private final SolicitudService solicitudService;
  private final ColeccionService coleccionService;

  public AgregadorController(SolicitudService solicitudService, ColeccionService coleccionService) {
    this.solicitudService = solicitudService;
    this.coleccionService = coleccionService;
  }

  //Panel control
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/resumen")
  public ResponseEntity<ResumenActividadDto> getResumenActividad() {
    ResumenActividadDto resumenActividadDto = coleccionService.getResumenActividad();
    return ResponseEntity.status(HttpStatus.OK).body(resumenActividadDto);
  }
  //COLECCIONES

  //CRUD COLECCIONES
  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones")
  public ResponseEntity<ColeccionDTOSalida> createColeccion(@RequestBody ColeccionDTOEntrada dto) {
    ColeccionDTOSalida coleccionCreada = coleccionService.createColeccion(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(coleccionCreada);
  }

  @GetMapping("/colecciones")
  public List<ColeccionDTOSalida> getColecciones() {
    return coleccionService.getColeccionesDTO();
  }

  @GetMapping("/colecciones/{id}")
  public ColeccionDTOSalida getColeccion(@PathVariable String id) {
    return coleccionService.getColeccionDTO(id);
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PutMapping("/colecciones/{id}")
  public void updateColeccion(@PathVariable String id, @RequestBody ColeccionDTOEntrada dto) {
    coleccionService.updateColeccion(id, dto);
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @DeleteMapping("/colecciones/{id}")
  public void deleteColeccion(@PathVariable String id) {
    coleccionService.deleteColeccion(id);
  }

  @GetMapping("/colecciones/{id}/hechos")
  public PaginacionDto<HechoDtoSalida> getHechos(
      @PathVariable String id,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false, defaultValue = "false") Boolean curados,
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) LocalDate fecha_acontecimiento_desde,
      @RequestParam(required = false) LocalDate fecha_acontecimiento_hasta,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String municipio,
      @RequestParam(required = false) String departamento
  ) {
    Set<IFiltroStrategy> filtros = FiltroStrategyFactory.fromParams(
        categoria,
        fecha_acontecimiento_desde,
        fecha_acontecimiento_hasta,
        provincia,
        municipio,
        departamento
    );

    return coleccionService.getHechos(id, curados, page, filtros);
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

  @PostMapping("/colecciones/{id}/filtros")
  public ResponseEntity<String> addCriterio(
      @PathVariable String id,
      @RequestBody FiltroDTOEntrada dto
  ) {
    IFiltroStrategy filtro = FiltroStrategyFactory.fromDTO(dto);
    coleccionService.addCriterio(id, filtro);
    return ResponseEntity.ok("Filtro agregado correctamente");
  }

  @PutMapping("/colecciones/normaliza")
  public void actualizarHechosCurados() {
    coleccionService.refrescarHechosCurados();
  }

  @PutMapping("/colecciones")
  public void actualiza() {coleccionService.refrescoFuentes();}

  //HECHOS
  @GetMapping("/hechos/{idHecho}")
  public ResponseEntity<HechoDetallesDtoSalida> obtenerHecho(
      @PathVariable Long idHecho) {
    HechoDetallesDtoSalida respuesta = coleccionService.getHechoDto(idHecho);
    return ResponseEntity.ok(respuesta);
  }
//  @PutMapping("/hechos/{idHecho}")
//  public ResponseEntity<String> actualizarHecho (
//      @PathVariable Long idHecho, @RequestBody HechoUpdateDto hechoDto) {
//    coleccionService.actualizarHecho(idHecho, hechoDto);
//    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Hecho actualizado");
//  }

  //SOLICITUDES
  @PostMapping("/solicitudes")
  public ResponseEntity<String> agregarSolicitud(@RequestBody SolicitudDTOEntrada dto) {
    solicitudService.createSolicitud(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud creada");
  }
  @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
  @GetMapping("/solicitudes")
  public PaginacionDto<SolicitudResumenDtoOutput> getSolicitudes(
      @RequestParam (required = false, defaultValue = "1") Integer page,
      @RequestParam (required = false, defaultValue = "true") Boolean pendientes
  ){
    return solicitudService.getSolicitudes(page, pendientes);
  }

  @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
  @GetMapping("/solicitudes/{id}")
  public SolicitudDTOOutput getSolicitud(
      @PathVariable Long id
  ){
    return solicitudService.getSolicitudDto(id);
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PutMapping("/solicitudes/{id}/aceptar")
  public ResponseEntity<String> aceptarSolicitud(
      @PathVariable Long id
  ) {
    solicitudService.aceptarSolicitud(id);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PutMapping("/solicitudes/{id}/denegar")
  public ResponseEntity<String> rechazarSolicitud(
      @PathVariable Long id
  ) {
    solicitudService.rechazarSolicitud(id);
    return ResponseEntity.noContent().build();
  }
}