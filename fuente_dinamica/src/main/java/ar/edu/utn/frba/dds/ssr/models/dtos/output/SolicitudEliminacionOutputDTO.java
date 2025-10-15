package ar.edu.utn.frba.dds.ssr.models.dtos.output;

import ar.edu.utn.frba.dds.ssr.models.enums.EstadoSolicitud;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SolicitudEliminacionOutputDTO {
    private Long id;
    private String titulo;
    private String texto;
    private EstadoSolicitud estado;
    private LocalDateTime fecha;
    private String responsable;
    private String supervisor;

    private Long hechoId;
}
