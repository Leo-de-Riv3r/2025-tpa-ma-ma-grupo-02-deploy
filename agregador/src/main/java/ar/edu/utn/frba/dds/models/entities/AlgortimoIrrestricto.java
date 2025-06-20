package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.util.Set;

//esta clase sera la que tendran las colecciones por defecto
public class AlgortimoIrrestricto extends AlgoritmoConsenso{
  @Override
  public Boolean cumpleConsenso(Hecho hecho, Set<IFuenteAbstract> fuentes, Set<FiltroStrategy> filtros) {
    cantidadMinimaApariciones = 0;
    return super.cumpleConsenso(hecho, fuentes, filtros);
  }
}
