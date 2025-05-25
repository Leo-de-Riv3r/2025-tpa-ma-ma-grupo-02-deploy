package ar.edu.utn.frba.dds.models.entities;

import lombok.Getter;

@Getter
public class Categoria {
  private final String nombre;

  public Categoria(String nombre) {
    this.nombre = nombre;
  }
}