package ar.edu.utn.frba.dds.models.DTO;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;

@AllArgsConstructor
  public class HechoDTO {
  //private Integer id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fecha_hecho;
  private LocalDateTime created_at;
  private LocalDateTime updated_at;
}
