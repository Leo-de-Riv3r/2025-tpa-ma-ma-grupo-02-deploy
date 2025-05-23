package ar.edu.utn.frba.dds.models.dtos.input;

import lombok.Data;

@Data
public class HechoInputDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  // TODO: Falta agregar atributo para texto y multimedia
}
