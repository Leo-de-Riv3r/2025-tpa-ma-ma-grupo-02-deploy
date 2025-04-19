package ar.edu.utn.frba.dds.Entities.Filtros;

import ar.edu.utn.frba.dds.Entities.Hecho;

public interface FiltroStrategy {
    public Boolean cumpleFiltro(Hecho hecho);
}
