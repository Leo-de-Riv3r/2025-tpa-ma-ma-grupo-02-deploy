package ar.edu.utn.frba.dds.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class HechoUpdateDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  @JsonProperty("fecha_hecho")
  private LocalDateTime fechaHecho;
}

