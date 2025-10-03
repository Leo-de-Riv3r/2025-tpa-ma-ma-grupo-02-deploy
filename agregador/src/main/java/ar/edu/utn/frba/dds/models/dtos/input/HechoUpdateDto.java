package ar.edu.utn.frba.dds.models.dtos.input;

import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class HechoUpdateDto {
  @JsonProperty("titulo")
  private String titulo;
  @JsonProperty("descripcion")
  private String descripcion;
  @JsonProperty("categoria")
  private String categoria;
  @JsonProperty("latitud")
  private Double latitud;
  @JsonProperty("longitud")
  private Double longitud;
  @JsonProperty("fecha_hecho")
  @JsonFormat(pattern = "yyyy-MM-dd")  //formateo cadena de texto a fecha
  private LocalDate fechaHecho;
  @JsonProperty("multimedia")
  private List<MultimediaDtoInput> multimedia;
}
