package ar.edu.utn.frba.dds.models.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Ubicacion {
  private final Double latitud;
  private final Double longitud;
  private String direccion;
  private String nombre;

  public Ubicacion(Double latitud, Double longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }

  public Ubicacion(Double latitud, Double longitud, String direccion, String nombre) {
    this.latitud = latitud;
    this.longitud = longitud;
    this.direccion = direccion;
    this.nombre = nombre;
  }

  public Boolean mismaUbicacion(Double latitud, Double longitud) {
    return this.latitud.equals(latitud) && this.longitud.equals(longitud);
  }
}