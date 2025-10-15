package ar.edu.utn.frba.dds.ssr.controllers;

import ar.edu.utn.frba.dds.ssr.models.dtos.input.SolicitudEliminacionInputDTO;
import ar.edu.utn.frba.dds.ssr.models.dtos.output.SolicitudEliminacionOutputDTO;
import ar.edu.utn.frba.dds.ssr.servicies.ISolicitudesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudesController {
    private final ISolicitudesService solicitudesService;

    public SolicitudesController(ISolicitudesService solicitudesService) {
        this.solicitudesService = solicitudesService;
    }

    @PostMapping("/eliminacion")
    public ResponseEntity<SolicitudEliminacionOutputDTO> crearSolicitudEliminacion(@RequestBody SolicitudEliminacionInputDTO solicitudDto) {
        try {
            SolicitudEliminacionOutputDTO solicitudCreada = solicitudesService.crearSolicitudEliminacion(solicitudDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
