package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;

public interface IFiltroStrategy {
  boolean cumpleFiltro(Hecho hecho);
}


