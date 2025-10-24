package ar.edu.utn.frba.dds.models.dtos.input;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

import java.util.List;

@Data
public class HechoInputDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDateTime fechaAcontecimiento;
  private String autor;
}
