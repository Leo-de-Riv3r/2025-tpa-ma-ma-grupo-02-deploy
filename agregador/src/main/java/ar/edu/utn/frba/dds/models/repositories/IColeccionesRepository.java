package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.IFuenteAdapter;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.util.List;
import java.util.Set;

public interface IColeccionesRepository {
  List<Coleccion> getColecciones();

  public void updateColeccion(String handler, Coleccion coleccion);

  public void cambiarFuentesColeccion(String handler, Set<IFuenteAdapter> fuentes);

  public void agregarFuente(String handler, IFuenteAdapter fuente);

  Coleccion findById(String handler);
}
