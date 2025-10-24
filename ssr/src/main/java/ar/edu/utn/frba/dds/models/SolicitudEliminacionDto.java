package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class SolicitudEliminacionDto {
  private Long idHecho;
  private String titulo;
  private String texto;
  private String creador;
}
