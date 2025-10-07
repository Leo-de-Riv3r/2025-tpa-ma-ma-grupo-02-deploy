package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.input.RespuestaSolicitudInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.SolicitudEliminacionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudEliminacionOutputDTO;
import ar.edu.utn.frba.dds.servicies.ISolicitudesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/solicitudes")
public class AdminSolicitudesController {
    private final ISolicitudesService solicitudesService;

    public AdminSolicitudesController(ISolicitudesService solicitudesService) {
        this.solicitudesService = solicitudesService;
    }

    @GetMapping("/pendientes")
    public List<SolicitudEliminacionOutputDTO> getSolicitudesPendientes() {
        return solicitudesService.getSolicitudesPendientes();
    }

    @PutMapping("/{id}/aceptar")
    public ResponseEntity<SolicitudEliminacionOutputDTO> aceptarSolicitud(@PathVariable Long id, @RequestBody RespuestaSolicitudInputDTO respuestaDto) {
        try {
            SolicitudEliminacionOutputDTO solicitudAceptada = solicitudesService.aceptarSolicitud(id, respuestaDto);
            return ResponseEntity.ok(solicitudAceptada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudEliminacionOutputDTO> rechazarSolicitud(@PathVariable Long id, @RequestBody RespuestaSolicitudInputDTO respuestaDto) {
        try {
            SolicitudEliminacionOutputDTO solicitudRechazada = solicitudesService.rechazarSolicitud(id, respuestaDto);
            return ResponseEntity.ok(solicitudRechazada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
