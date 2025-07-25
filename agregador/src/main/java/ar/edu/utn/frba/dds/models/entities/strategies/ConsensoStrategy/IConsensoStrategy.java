package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.Set;

public abstract class IConsensoStrategy {
  protected Integer cantidadMinimaApariciones;

  protected Boolean cumpleConsensoBase(Hecho hecho, Set<Fuente> fuentes, Set<IFiltroStrategy> filtros, Integer cantMin) {
    long apariciones = fuentes.stream()
        .map(fuente -> fuente.getHechos(filtros))
        .filter(hechos -> hechos.stream()
            .anyMatch(h -> h.equals(hecho)))
        .count();

    return apariciones >= cantMin;
  }

  public abstract Boolean cumpleConsenso(Hecho hecho, Set<Fuente> fuentes, Set<IFiltroStrategy> filtros);
}
