package ar.edu.utn.frba.dds.ssr.controllers;

import ar.edu.utn.frba.dds.ssr.models.dtos.input.RevisionInputDTO;
import ar.edu.utn.frba.dds.ssr.models.dtos.output.HechoRevisionOutputDTO;
import ar.edu.utn.frba.dds.ssr.servicies.IHechosService;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hechos")
public class AdminHechosController {
  private final IHechosService hechosService;

  public AdminHechosController(IHechosService hechosService) {
    this.hechosService = hechosService;
  }

  @GetMapping("/pendientes")
  public List<HechoRevisionOutputDTO> getHechosPendientes() {
    return hechosService.getHechosPendientes();
  }

  @PutMapping("/{id}/aceptar")
  public ResponseEntity<HechoRevisionOutputDTO> aceptarHecho(@PathVariable Long id, @RequestBody RevisionInputDTO revisionDto) {
    try {
      HechoRevisionOutputDTO hechoAceptado = hechosService.aceptarHecho(id, revisionDto);
      return ResponseEntity.ok(hechoAceptado);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/aceptar-con-sugerencias")
  public ResponseEntity<HechoRevisionOutputDTO> aceptarHechoConSugerencias(@PathVariable Long id, @RequestBody RevisionInputDTO revisionDto) {
    try {
      HechoRevisionOutputDTO hechoAceptado = hechosService.aceptarHechoConSugerencias(id, revisionDto);
      return ResponseEntity.ok(hechoAceptado);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/rechazar")
  public ResponseEntity<HechoRevisionOutputDTO> rechazarHecho(
          @PathVariable Long id,
          @RequestBody RevisionInputDTO revisionDto) {
    try {
      HechoRevisionOutputDTO hechoRechazado = hechosService.rechazarHecho(id, revisionDto);
      return ResponseEntity.ok(hechoRechazado);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
