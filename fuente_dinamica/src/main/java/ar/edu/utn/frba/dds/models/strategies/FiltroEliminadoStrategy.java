package ar.edu.utn.frba.dds.models.strategies;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.util.HashSet;
import java.util.Set;

public class FiltroEliminadoStrategy implements IFiltroStrategy {
  private Set<Hecho> eliminados;

  public FiltroEliminadoStrategy() {
    this.eliminados = new HashSet<>();
  }

  @Override
  public boolean cumpleFiltro(Hecho hecho) {
    return !this.eliminados.contains(hecho);
  }

  public void agregarEliminado(Hecho hecho) {
    this.eliminados.add(hecho);
  }
}