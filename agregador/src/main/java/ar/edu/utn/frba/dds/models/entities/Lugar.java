package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class Lugar {
  @Column
  private String departamento;
  @Column
  private String provincia;
  @Column
  private String municipio;

  public Lugar() {
    this.departamento = "";
    this.provincia = "";
    this.municipio = "";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Lugar lugar))
      return false;
    return Objects.equals(provincia, lugar.provincia) &&
        Objects.equals(municipio, lugar.municipio) &&
        Objects.equals(departamento, lugar.departamento);
  }

  @Override
  public int hashCode() {
    return Objects.hash(provincia, municipio, departamento);
  }
}