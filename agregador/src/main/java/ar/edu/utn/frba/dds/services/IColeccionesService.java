package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.IFuenteAdapter;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import java.util.List;
import java.util.Set;

public interface IColeccionesService {
  List<Coleccion> getColecciones();

  void refrescoColecciones();

  void setFuentesColeccion(String handler, Set<IFuenteAdapter> fuentes);

  Hecho convertirHechoDTOAHecho(Object hecho, TipoOrigen tipoOrigen);

  void actualizarHechosFuentes(Coleccion coleccion, Integer page, Integer per_page);

  void actualizarHechosFuentes(Coleccion coleccion);

  //obtener todos los hechos
  List<Hecho> obtenerHechos();

  List<Hecho> obtenerHechos(Integer page, Integer per_page);

  List<Hecho> obtenerHechos(String handler);

  List<Hecho> obtenerHechos(String handler, Integer page, Integer per_page);

  List<Hecho> consultarHechos(IFuenteAdapter fuente);

  List<Hecho> consultarHechos(IFuenteAdapter fuente, Integer page, Integer per_page);

  void agregarFuente(String handler, IFuenteAdapter fuente);
}
