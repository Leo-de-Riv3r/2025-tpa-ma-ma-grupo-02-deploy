package ar.edu.utn.frba.dds.strategies;

import ar.edu.utn.frba.dds.entities.Hecho;

public interface FiltroStrategy {
    boolean cumpleFiltro(Hecho hecho);
}
