package ar.edu.utn.frba.dds.models.entities;

import lombok.Getter;

@Getter
public class Etiqueta {
  private final String nombre;

  public Etiqueta(String nombre) {
    this.nombre = nombre;
  }
}