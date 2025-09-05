package ar.edu.utn.frba.dds.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "contribuyentes")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contribuyente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;
    // TODO: Terminar de implementar relación
    private List<Hecho> contribuciones;


    public void agregarContribucion(Long id) {
        this.contribuciones.add(id);
    }

    public boolean tieneHecho(Long id) {
        return contribuciones.contains(id);
    }
}
