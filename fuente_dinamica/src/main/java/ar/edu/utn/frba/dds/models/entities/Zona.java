package ar.edu.utn.frba.dds.models.entities;

import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "zona")
@AllArgsConstructor
@NoArgsConstructor
public class Zona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Set<Ubicacion> perimetro;

    public Boolean pertenece(Ubicacion ubicacion) {
    return perimetro.contains(ubicacion);
  }
}
