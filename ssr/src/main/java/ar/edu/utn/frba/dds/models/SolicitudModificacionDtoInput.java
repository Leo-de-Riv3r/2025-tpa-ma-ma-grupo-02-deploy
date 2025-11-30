package ar.edu.utn.frba.dds.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SolicitudModificacionDtoInput {
  private Long id;
  private Long idHecho;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fecha;
  private LocalDate fechaAcontecimiento;
  private String creador;
}
