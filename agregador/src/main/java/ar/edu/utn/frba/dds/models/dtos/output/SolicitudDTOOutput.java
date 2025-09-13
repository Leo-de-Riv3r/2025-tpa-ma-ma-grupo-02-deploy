package ar.edu.utn.frba.dds.models.dtos.output;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitudDTOOutput {
    private String id;
    private String titulo;
    private String motivo;
    private LocalDateTime fecha;
    private String estadoActual;
    private String responsable;
    private String supervisor;
    private Integer esSpam;
    private Long idHecho;
}
