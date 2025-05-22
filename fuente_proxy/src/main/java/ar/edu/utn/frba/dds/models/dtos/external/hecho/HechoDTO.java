package ar.edu.utn.frba.dds.models.dtos.external.hecho;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HechoDTO {
  private int id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private double latitud;
  private double longitud;
  private LocalDateTime fecha_hecho;
  private LocalDateTime created_at;
  private LocalDateTime updated_at;
}
