package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter@Setter
public class Lugar {
  @Column
  private String departamento;
  @Column
  private String provincia;
  @Column
  private String municipio;

  public Lugar() {
  }
}
