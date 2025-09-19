package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//@Embeddable
@Embeddable
public class Ubicacion {
  //@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column
  private Double latitud;
  @Column
  private  Double longitud;
  @Embedded
  private Lugar lugar;

  public Ubicacion() {
  }


  public Boolean mismaUbicacion(Double latitud, Double longitud) {
    return this.latitud.equals(latitud) && this.longitud.equals(longitud);
  }
}