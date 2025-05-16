package ar.edu.utn.frba.dds.models.entities.strategies;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Zona;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FiltroZonaStrategy implements FiltroStrategy {
    private Zona zona;

    @Override
    public boolean cumpleFiltro(Hecho hecho) {
        return zona.pertenece(hecho.getUbicacion());
    }
}
