package com.ddsi.utn.ba.ssr.models;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class HechoDetallesDto {
  private Long id;
  private String titulo;
  private String descripcion;
  private Double latitud;
  private Double longitud;
  private String categoria;
  private String provincia;
  private String municipio;
  private String departamento;
  private String tipoOrigen;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  private List<MultimediaDto> multimedia;
  private String nombreAutor;
}
