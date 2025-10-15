package ar.edu.utn.frba.dds.models.dtos.input;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

import java.util.List;

@Data
public class HechoInputDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;

  @JsonSetter(nulls = Nulls.AS_EMPTY)
  private List<MultimediaInputDTO> multimedia;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDateTime fechaAcontecimiento;
}
