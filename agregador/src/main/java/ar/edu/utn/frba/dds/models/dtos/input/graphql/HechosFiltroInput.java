package ar.edu.utn.frba.dds.models.dtos.input.graphql;

import java.time.LocalDate;
import lombok.Data;
import lombok.Getter;

@Data
public class HechosFiltroInput {
  private String categoria;
  private String fecha_acontecimiento_desde;
  private String fecha_acontecimiento_hasta;
  private String provincia;
  private String municipio;
  private String departamento;
}