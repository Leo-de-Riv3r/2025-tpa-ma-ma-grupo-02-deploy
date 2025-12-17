package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Setter
@Getter
@Embeddable
public class Ubicacion {
  @Column
  private Double latitud;
  @Column
  private Double longitud;
  @Embedded
  private Lugar lugar;

  public Ubicacion() {
  }

  public Boolean mismaUbicacion(Double latitud, Double longitud) {
    return this.latitud.equals(latitud) && this.longitud.equals(longitud);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Ubicacion ubicacion))
      return false;
    return Objects.equals(lugar, ubicacion.lugar);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lugar);
  }
}