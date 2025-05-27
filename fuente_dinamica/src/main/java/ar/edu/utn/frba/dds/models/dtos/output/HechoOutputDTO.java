package ar.edu.utn.frba.dds.models.dtos.output;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HechoOutputDTO {
  //private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fecha_hecho;
  private LocalDateTime created_at;
  // private LocalDateTime updated_at;
  // private List<MultimediaOutputDTO> multimedia;
}
