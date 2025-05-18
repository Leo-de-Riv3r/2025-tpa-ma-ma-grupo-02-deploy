package ar.edu.utn.frba.dds.models.entities.FuentesDatos;

import ar.edu.utn.frba.dds.models.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.util.Set;

public class FuenteEstatica implements FuenteDeDatos {
  @Override
  private String url;
  public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios) {
    return Set.of();
  }

  @Override
  public boolean eliminarHecho(Hecho hecho) {
    return false;
  }
}
