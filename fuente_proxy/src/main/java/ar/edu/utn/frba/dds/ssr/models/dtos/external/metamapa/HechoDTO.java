package ar.edu.utn.frba.dds.ssr.models.dtos.external.metamapa;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class HechoDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  private List<String> etiquetas;
  private UbicacionDTO ubicacion;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  private String origen;
  private MultimediaDTO multimedia;
}
