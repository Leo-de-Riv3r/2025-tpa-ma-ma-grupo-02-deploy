package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.AlgoritmoConsenso;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.IFuenteAbstract;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IColeccionesRepository {
  List<Coleccion> getColecciones();

  public void updateColeccion(String handler, Coleccion coleccion);

  public void agregarFuente(String handler, IFuenteAbstract fuente);

  Optional<Coleccion> findById(String handler);

  Coleccion deleteColeccion(String id);

  void eliminarFuente(String idColeccion, String idFuente);

  void setAlgoritmoConsenso(String id, AlgoritmoConsenso algoritmoConsenso);

  void createColeccion(Coleccion coleccion);

  void actualizarFuentes();

  void actualizarHechosConsensuados();
}
