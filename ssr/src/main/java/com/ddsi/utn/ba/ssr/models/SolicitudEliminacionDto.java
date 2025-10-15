package com.ddsi.utn.ba.ssr.models;

import lombok.Data;

@Data
public class SolicitudEliminacionDto {
  private Long idHecho;
  private String titulo;
  private String texto;
  private String creador;
}
