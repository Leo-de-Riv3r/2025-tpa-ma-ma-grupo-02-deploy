package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.enums.TipoLugar;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Lugar {
    @Column(name = "nombre_lugar")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_lugar")
    private TipoLugar tipo;
}