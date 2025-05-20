package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import java.util.List;

public interface IColeccionesRepository {
  List<Coleccion> getColecciones();

  void updateColeccion(String handler, Coleccion coleccion);
}
