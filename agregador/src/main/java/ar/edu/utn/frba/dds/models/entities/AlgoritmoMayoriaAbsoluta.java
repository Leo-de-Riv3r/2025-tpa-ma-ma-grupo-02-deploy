package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.util.Set;

public class AlgoritmoMayoriaAbsoluta extends AlgoritmoConsenso {
  @Override
  public Boolean cumpleConsenso(Hecho hecho, Set<IFuenteAbstract> fuentes, Set<FiltroStrategy> filtros) {
    cantidadMinimaApariciones = fuentes.size();
    return super.cumpleConsenso(hecho, fuentes, filtros);
  }
}
