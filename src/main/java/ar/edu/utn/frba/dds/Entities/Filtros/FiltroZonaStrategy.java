package ar.edu.utn.frba.dds.Entities.Filtros;

import ar.edu.utn.frba.dds.Entities.Hecho;
import ar.edu.utn.frba.dds.Entities.Zona;

public class FiltroZonaStrategy implements FiltroStrategy {
    private Zona zona;

    public FiltroZonaStrategy(Zona zona) {
        this.zona = zona;
    }

    @Override
    public Boolean cumpleFiltro(Hecho hecho) {
        return zona.perteneceAZona(hecho.getUbicacion());
    }
}
