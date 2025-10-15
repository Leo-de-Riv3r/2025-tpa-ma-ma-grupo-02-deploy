package ar.edu.utn.frba.dds.ssr.models.strategies;

import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;

public interface IFiltroStrategy {
  boolean cumpleFiltro(Hecho hecho);
}
