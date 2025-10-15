package ar.edu.utn.frba.dds.ssr.models;

import lombok.Data;

@Data
public class DetalleEstadisticaDto {
  private Long id;
  private String categoriaMayoresHechos;
  private String provinciaMayorCantHechos;
  private String provinciaMayorCantHechosCategoria;
  private Number horaMayorCantHechos;
  private Integer solicitudesSpam;
}
