package ar.edu.utn.frba.dds.entities;

import java.util.Set;
import lombok.Getter;

@Getter
public class Zona {
  private String nombre;
  private Set<Ubicacion> perimetro;

  public Zona(String nombre, Set<Ubicacion> perimetro) {
    this.nombre = nombre;
    this.perimetro = perimetro;
  }

  public Boolean pertenece(Ubicacion ubicacion) {
    return perimetro.contains(ubicacion);
  }
}
