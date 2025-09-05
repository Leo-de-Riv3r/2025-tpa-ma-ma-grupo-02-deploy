package ar.edu.utn.frba.dds.estadisticas.controllers;

import ar.edu.utn.frba.dds.estadisticas.models.dto.input.EstadisticaNuevaDTO;
import ar.edu.utn.frba.dds.estadisticas.models.entities.Estadistica;
import ar.edu.utn.frba.dds.estadisticas.services.IEstadisticasService;
import ar.edu.utn.frba.dds.estadisticas.services.impl.EstadisticasService;
import java.util.List;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstadisticasController {
  private final IEstadisticasService estadisticasService;
  public EstadisticasController (IEstadisticasService estadisticasService) {
    this.estadisticasService = estadisticasService;
  }

  @PostMapping("/api/estadisticas")
  public Estadistica crearEstadistica(@RequestBody EstadisticaNuevaDTO dto) {
    System.out.println("CREAR");
    return estadisticasService.createEstadistica(dto);
  }

  @PutMapping("/api/estadisticas")
  public ResponseEntity<String> actualizarEstadisticas() {
    estadisticasService.actualizarEstadisticas();
    return ResponseEntity.ok("Estadisticas actualizadas");
  }

  @GetMapping("/api/estadisticas")
  public List<Estadistica> getEstadisticas() {
    return estadisticasService.getEstadisticas();
  }

  @GetMapping("/api/estadisticas/{id}")
  public Estadistica getEstadisticas(@RequestParam Long estadisticaId) {
    return estadisticasService.getEstadisticaById(estadisticaId);
  }

  @DeleteMapping("/api/estadisticas/{id}")
  public  ResponseEntity<String> deleteEstadistica(@RequestBody Long estadisticaId) {
    estadisticasService.eliminarEstadistica(estadisticaId);
    return ResponseEntity.ok("Operacion completada");
  }
}
