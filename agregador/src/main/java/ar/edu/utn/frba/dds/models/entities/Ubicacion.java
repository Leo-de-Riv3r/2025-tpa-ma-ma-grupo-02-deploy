package ar.edu.utn.frba.dds.models.entities;

import lombok.Getter;

@Getter
public class Ubicacion {
  private Double latitud;
  private Double longitud;
  private String direccion;
  private Lugar referenciaLugar;

  public Ubicacion(Double latitud, Double longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }
}