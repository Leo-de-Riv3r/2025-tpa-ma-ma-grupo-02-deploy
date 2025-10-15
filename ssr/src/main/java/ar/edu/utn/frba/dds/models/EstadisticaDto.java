package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class EstadisticaDto {
  private Long id;
  private String urlColeccion;
  private String nombre;
  private String categoriaEspecifica;
  private DetalleEstadisticaDto detalle;
  private int vigente = 1;
}
