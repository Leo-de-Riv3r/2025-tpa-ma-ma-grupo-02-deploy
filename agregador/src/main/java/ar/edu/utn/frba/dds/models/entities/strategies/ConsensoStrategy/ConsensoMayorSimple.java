package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.Set;

public class ConsensoMayorSimple extends IConsensoStrategy {
  @Override
  public Boolean cumpleConsenso(Hecho hecho, Set<Fuente> fuentes, Set<IFiltroStrategy> filtros) {
    Integer mitad = fuentes.size() / 2;
    return cumpleConsensoBase(hecho, fuentes, filtros, mitad);
  }
}

