package ar.edu.utn.frba.dds.models.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Categoria {
    private Long id;
    private String nombre;

    public Categoria(String nombre) {
        this.nombre = nombre;
    }
}
