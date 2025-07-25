package ar.edu.utn.frba.dds.models.dtos;

import lombok.Data;

@Data
public class SolicitudDTOEntrada {
  private String titulo;
  private String texto;
  private String responsable;
  private String tituloHecho;
}
