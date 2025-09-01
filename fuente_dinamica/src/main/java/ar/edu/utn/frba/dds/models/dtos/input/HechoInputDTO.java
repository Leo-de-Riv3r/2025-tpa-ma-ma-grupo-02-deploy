package ar.edu.utn.frba.dds.models.dtos.input;

import java.time.LocalDateTime;
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
  private List<MultimediaInputDTO> multimedia;
  private String fechaAcontecimiento;
}
