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

  public Ubicacion(Double latitud, Double longitud, String direccion, Lugar referenciaLugar) {
    this.latitud = latitud;
    this.longitud = longitud;
    this.direccion = direccion;
    this.referenciaLugar = referenciaLugar;
  }
}