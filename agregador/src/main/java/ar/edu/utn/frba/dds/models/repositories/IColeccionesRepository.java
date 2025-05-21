package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.util.List;
import java.util.Set;

public interface IColeccionesRepository {
  List<Coleccion> getColecciones();

  public void updateColeccion(String handler, Coleccion coleccion);

  public void cambiarFuentesColeccion(String handler, Set<FuenteDeDatos> fuentes);

  public void agregarFuente(String handler, FuenteDeDatos fuente);

  public void agregarHechoTiempoReal(String handler, FuenteDeDatos fuente, Hecho hecho);

}
