package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.AlgoritmoConsenso;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.IFuenteAbstract;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import java.util.List;
import java.util.Set;

public interface IColeccionesService {
  List<Coleccion> getColecciones();

  void refrescoColecciones();


  Hecho convertirHechoDTOAHecho(Object hecho, TipoOrigen tipoOrigen);

  void actualizarHechosFuentes(Coleccion coleccion);

  //obtener todos los hechos
  Set<Hecho> obtenerHechos();

  Set<Hecho> obtenerHechos(Integer page, Integer per_page);

  Set<Hecho> obtenerHechos(String handler);

  Set<Hecho> obtenerHechos(String handler, Integer page, Integer per_page);

  void agregarFuente(String handler, IFuenteAbstract fuente);


  void eliminarFuente(String handler, String idFuente);

  void actualizarColeccion(String id, Coleccion coleccion);

  void eliminarColeccion(String id);

  void crearColeccion(Coleccion coleccion);

  void cambiarAlgoritmoConsenso(String id, AlgoritmoConsenso algoritmoConsenso);

  public List<Hecho> obtenerHechosCurados(String id);

  void actualizarHechosConsensuados();
}
