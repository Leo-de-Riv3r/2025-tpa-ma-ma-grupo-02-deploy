package ar.edu.utn.frba.dds.models.entities;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contribuyente {
    private Long id;
    private String email;
    private String password;
    private List<Hecho> contribuciones;

    public void agregarContribucion(Hecho hecho) {
        this.contribuciones.add(hecho);
    }
}
