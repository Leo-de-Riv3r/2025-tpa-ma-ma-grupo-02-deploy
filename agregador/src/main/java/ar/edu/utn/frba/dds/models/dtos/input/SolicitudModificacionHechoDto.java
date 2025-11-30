package ar.edu.utn.frba.dds.models.dtos.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SolicitudModificacionHechoDto {
  private Long idHecho;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime fechaAcontecimiento;
  private String creador;
}
