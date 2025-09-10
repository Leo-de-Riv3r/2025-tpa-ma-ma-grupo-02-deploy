package ar.edu.utn.frba.dds.estadisticas.models.dto.input;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HechoDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private UbicacionDTO ubicacion;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
}
