package ar.edu.utn.frba.dds.estadisticas.controllers;

import ar.edu.utn.frba.dds.estadisticas.models.dto.input.EstadisticaNuevaDTO;
import ar.edu.utn.frba.dds.estadisticas.models.entities.Estadistica;
import ar.edu.utn.frba.dds.estadisticas.services.IEstadisticasService;
import ar.edu.utn.frba.dds.estadisticas.services.impl.EstadisticasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Estadísticas", description = "API para la gestión de estadísticas")
public class EstadisticasController {
  private final IEstadisticasService estadisticasService;
  public EstadisticasController (IEstadisticasService estadisticasService) {
    this.estadisticasService = estadisticasService;
  }

  @Operation(summary = "Crear una nueva estadística", description = "Crea un nuevo registro de estadística sobre una coleccion")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Estadística creada exitosamente",
          content = { @Content(mediaType = "application/json",
              schema = @Schema(implementation = Estadistica.class)) }),
      @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
  })
  @PostMapping("/api/estadisticas")
  public Estadistica crearEstadistica(@RequestBody EstadisticaNuevaDTO dto) {
    return estadisticasService.createEstadistica(dto);
  }

//  @PutMapping("/api/estadisticas")
//  public ResponseEntity<String> actualizarEstadisticas() {
//    estadisticasService.actualizarEstadisticas();
//    return ResponseEntity.ok("Estadisticas actualizadas");
//  }

  @Operation(summary = "Obtener todas las estadísticas", description = "Devuelve una lista con todas las estadísticas almacenadas.")
  @ApiResponse(responseCode = "200", description = "Lista de estadísticas",
      content = { @Content(mediaType = "application/json",
          schema = @Schema(implementation = Estadistica.class)) })
  @GetMapping("/api/estadisticas")
  public List<Estadistica> getEstadisticas() {
    return estadisticasService.getEstadisticas();
  }


  @Operation(summary = "Obtener una estadística por su ID", description = "Busca y devuelve una estadística específica utilizando su ID numérico.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Estadística encontrada",
          content = { @Content(mediaType = "application/json",
              schema = @Schema(implementation = Estadistica.class)) }),
      @ApiResponse(responseCode = "404", description = "Estadística no encontrada", content = @Content)
  })
  @GetMapping("/api/estadisticas/{estadisticaId}")
  public Estadistica getEstadisticas(
      @Parameter(description = "ID de la estadística a buscar", required = true, example = "1")
      @PathVariable Long estadisticaId) {
    return estadisticasService.getEstadisticaById(estadisticaId);
  }

  @Operation(summary = "Eliminar una estadística", description = "Elimina de forma permanente una estadística del sistema a través de su ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Operacion completada"),
      @ApiResponse(responseCode = "404", description = "Estadística con id ... no encontrada")
  })
  @DeleteMapping("/api/estadisticas/{estadisticaId}")
  public  ResponseEntity<String> deleteEstadistica(
      @Parameter(description = "ID de la estadística a eliminar", required = true, example = "1")
      @PathVariable Long estadisticaId) {
    estadisticasService.eliminarEstadistica(estadisticaId);
    return ResponseEntity.ok("Operacion completada");
  }
}
