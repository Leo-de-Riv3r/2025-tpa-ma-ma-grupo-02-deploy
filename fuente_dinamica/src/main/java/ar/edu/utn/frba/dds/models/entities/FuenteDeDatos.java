package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.strategies.IFiltroStrategy;
import java.util.Set;

public interface FuenteDeDatos {
  public Set<Hecho> obtenerHechos(Set<IFiltroStrategy> criterios);
  public boolean eliminarHecho(Hecho hecho);
}
