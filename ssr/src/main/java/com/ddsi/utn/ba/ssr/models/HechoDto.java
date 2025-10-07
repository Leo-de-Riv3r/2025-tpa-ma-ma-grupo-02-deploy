package com.ddsi.utn.ba.ssr.models;

import lombok.Data;

@Data
public class HechoDto {
  private Long id;
  private String titulo;
  private String categoria;
  private String provincia;
  private String municipio;
  private String departamento;
  private Double latitud;
  private Double longitud;
  private String tipoFuente;
}
