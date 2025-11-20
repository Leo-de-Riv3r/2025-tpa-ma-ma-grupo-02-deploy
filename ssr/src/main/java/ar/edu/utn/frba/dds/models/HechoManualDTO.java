package ar.edu.utn.frba.dds.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HechoManualDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDateTime fechaAcontecimiento;
  private String autor;
}
