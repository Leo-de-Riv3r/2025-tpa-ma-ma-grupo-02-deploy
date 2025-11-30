package ar.edu.utn.frba.dds.models.dtos.output;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SolicitudModificacionHechoDtoOutput {
  private Long id;
  private Long idHecho;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fecha;
  private String creador;
}
