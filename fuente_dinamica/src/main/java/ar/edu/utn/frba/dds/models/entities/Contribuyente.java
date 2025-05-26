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
    private List<Long> contribuciones;

    public void agregarContribucion(Long id) {
        this.contribuciones.add(id);
    }

    public boolean tieneHecho(Long id) {
        return contribuciones.contains(id);
    }
}
