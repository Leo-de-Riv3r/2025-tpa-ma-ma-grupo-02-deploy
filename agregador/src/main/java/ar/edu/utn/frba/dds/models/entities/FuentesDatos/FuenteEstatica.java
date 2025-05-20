package ar.edu.utn.frba.dds.models.entities.FuentesDatos;

import ar.edu.utn.frba.dds.models.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.util.List;
import java.util.Set;

public class FuenteEstatica implements FuenteDeDatos {
  private String url;

  @Override
  public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios) {
    return Set.of();
  }

  @Override
  public boolean eliminarHecho(Hecho hecho) {
    return false;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public void setHechos(List<Hecho> hechosActualizados) {

  }
}
