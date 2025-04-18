package ar.edu.utn.frba.dds.Entities.Filtros;

import ar.edu.utn.frba.dds.Entities.Hecho;
import ar.edu.utn.frba.dds.Entities.Zona;

public class FiltroZona implements IFiltroAdapter {
    private Zona zona;

    public FiltroZona(Zona zona) {
        this.zona = zona;
    }

    @Override
    public Boolean cumpleFiltro(Hecho hecho) {
        return zona.perteneceAZona(hecho.getUbicacion());
    }
}
