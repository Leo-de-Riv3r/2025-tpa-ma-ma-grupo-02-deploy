package ar.edu.utn.frba.dds.models.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class Ubicacion {
    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "direccion")
    private String direccion;

    @Embedded
    private Lugar referenciaLugar;

    public Ubicacion(Double latitud, Double longitud) {
      this.latitud = latitud;
      this.longitud = longitud;
    }
}
